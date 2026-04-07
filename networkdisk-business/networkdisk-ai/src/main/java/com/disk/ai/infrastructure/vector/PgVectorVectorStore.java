package com.disk.ai.infrastructure.vector;

import com.disk.ai.infrastructure.file.AiSourceFile;
import com.disk.ai.infrastructure.parser.ParsedDocument;
import com.pgvector.PGvector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@ConditionalOnProperty(name = "com.disk.ai.pgvector.enabled", havingValue = "true")
public class PgVectorVectorStore implements VectorStore, InitializingBean {

    private final PgVectorProperties properties;

    private final JdbcTemplate jdbcTemplate;

    public PgVectorVectorStore(PgVectorProperties properties,
                               @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        if (properties.isInitSchema()) {
            initializeSchema();
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public DocumentIndexSummary getIndexSummary(Long userId, Long userFileId) {
        List<DocumentIndexSummary> results = jdbcTemplate.query(
                "select filename, media_type, parser, block_count, chunk_count, content_length from ai_document_index where user_id = ? and user_file_id = ?",
                (rs, rowNum) -> {
                    DocumentIndexSummary summary = new DocumentIndexSummary();
                    summary.setFilename(rs.getString("filename"));
                    summary.setMediaType(rs.getString("media_type"));
                    summary.setParser(rs.getString("parser"));
                    summary.setBlockCount(rs.getInt("block_count"));
                    summary.setChunkCount(rs.getInt("chunk_count"));
                    summary.setContentLength(rs.getLong("content_length"));
                    summary.setVectorDimension(properties.getDimension());
                    return summary;
                },
                userId,
                userFileId
        );
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<String> loadDocumentChunks(Long userId, Long userFileId) {
        return jdbcTemplate.queryForList(
                "select chunk_text from ai_document_chunk_vector where user_id = ? and user_file_id = ? order by chunk_index asc",
                String.class,
                userId,
                userFileId
        );
    }

    @Override
    public void replaceDocument(AiSourceFile sourceFile, ParsedDocument parsedDocument, List<PgVectorDocumentChunk> chunks) {
        jdbcTemplate.update("delete from ai_document_chunk_vector where user_id = ? and user_file_id = ?", sourceFile.getUserId(), sourceFile.getUserFileId());

        jdbcTemplate.update("""
                insert into ai_document_index(
                    user_id, user_file_id, real_file_id, filename, file_suffix, media_type, parser,
                    content_length, plain_text_chars, block_count, chunk_count, gmt_modified
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)
                on conflict (user_id, user_file_id)
                do update set real_file_id = excluded.real_file_id,
                              filename = excluded.filename,
                              file_suffix = excluded.file_suffix,
                              media_type = excluded.media_type,
                              parser = excluded.parser,
                              content_length = excluded.content_length,
                              plain_text_chars = excluded.plain_text_chars,
                              block_count = excluded.block_count,
                              chunk_count = excluded.chunk_count,
                              gmt_modified = current_timestamp
                """,
                sourceFile.getUserId(),
                sourceFile.getUserFileId(),
                sourceFile.getRealFileId(),
                sourceFile.getFilename(),
                sourceFile.getFileSuffix(),
                parsedDocument.getMediaType(),
                parsedDocument.getParser(),
                (long) sourceFile.getBytes().length,
                parsedDocument.getPlainText().length(),
                parsedDocument.getBlocks().size(),
                chunks.size()
        );

        jdbcTemplate.batchUpdate("""
                insert into ai_document_chunk_vector(
                    user_id, user_file_id, real_file_id, filename, file_suffix, media_type, parser,
                    block_index, chunk_index, start_offset, end_offset, token_estimate, chunk_text, embedding
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PgVectorDocumentChunk chunk = chunks.get(i);
                ps.setLong(1, chunk.getUserId());
                ps.setLong(2, chunk.getUserFileId());
                ps.setLong(3, chunk.getRealFileId());
                ps.setString(4, chunk.getFilename());
                ps.setString(5, chunk.getFileSuffix());
                ps.setString(6, chunk.getMediaType());
                ps.setString(7, chunk.getParser());
                ps.setInt(8, chunk.getBlockIndex());
                ps.setInt(9, chunk.getChunkIndex());
                ps.setInt(10, chunk.getStartOffset());
                ps.setInt(11, chunk.getEndOffset());
                ps.setInt(12, chunk.getTokenEstimate());
                ps.setString(13, chunk.getChunkText());
                ps.setObject(14, new PGvector(chunk.getEmbedding()));
            }

            @Override
            public int getBatchSize() {
                return chunks.size();
            }
        });
    }

    @Override
    public List<PgVectorSearchResult> search(Long userId, Long userFileId, float[] queryVector, int topK) {
        PGvector vector = new PGvector(queryVector);
        return jdbcTemplate.query(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            select chunk_index, block_index, chunk_text, 1 - (embedding <=> ?) as similarity
                            from ai_document_chunk_vector
                            where user_id = ? and user_file_id = ?
                            order by embedding <=> ?
                            limit ?
                            """);
                    ps.setObject(1, vector);
                    ps.setLong(2, userId);
                    ps.setLong(3, userFileId);
                    ps.setObject(4, vector);
                    ps.setInt(5, topK);
                    return ps;
                },
                (rs, rowNum) -> {
                    PgVectorSearchResult result = new PgVectorSearchResult();
                    result.setChunkIndex(rs.getInt("chunk_index"));
                    result.setBlockIndex(rs.getInt("block_index"));
                    result.setChunkText(rs.getString("chunk_text"));
                    result.setSimilarity(rs.getDouble("similarity"));
                    return result;
                }
        );
    }

    private void initializeSchema() {
        jdbcTemplate.execute("create extension if not exists vector");
        jdbcTemplate.execute("""
                create table if not exists ai_document_index (
                    id bigserial primary key,
                    user_id bigint not null,
                    user_file_id bigint not null,
                    real_file_id bigint not null,
                    filename varchar(512) not null,
                    file_suffix varchar(64),
                    media_type varchar(128),
                    parser varchar(128),
                    content_length bigint not null default 0,
                    plain_text_chars integer not null default 0,
                    block_count integer not null default 0,
                    chunk_count integer not null default 0,
                    gmt_create timestamp not null default current_timestamp,
                    gmt_modified timestamp not null default current_timestamp,
                    unique (user_id, user_file_id)
                )
                """);
        jdbcTemplate.execute(String.format("""
                create table if not exists ai_document_chunk_vector (
                    id bigserial primary key,
                    user_id bigint not null,
                    user_file_id bigint not null,
                    real_file_id bigint not null,
                    filename varchar(512) not null,
                    file_suffix varchar(64),
                    media_type varchar(128),
                    parser varchar(128),
                    block_index integer not null,
                    chunk_index integer not null,
                    start_offset integer not null default 0,
                    end_offset integer not null default 0,
                    token_estimate integer not null default 0,
                    chunk_text text not null,
                    embedding vector(%d) not null,
                    gmt_create timestamp not null default current_timestamp
                )
                """, properties.getDimension()));
        jdbcTemplate.execute("create index if not exists idx_ai_document_chunk_owner on ai_document_chunk_vector(user_id, user_file_id)");
    }
}
