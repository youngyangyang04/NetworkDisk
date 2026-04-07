package com.disk.ai.infrastructure.result;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.vector.PgVectorProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(name = "com.disk.ai.pgvector.enabled", havingValue = "true")
public class PgDocumentResultStore implements DocumentResultStore, InitializingBean {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final PgVectorProperties properties;

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public PgDocumentResultStore(PgVectorProperties properties,
                                 @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
                                 ObjectMapper objectMapper) {
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
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
    public StoredDocumentSummary getSummary(Long userId, Long userFileId) {
        List<StoredDocumentSummary> results = jdbcTemplate.query(
                """
                        select filename, summary_text, summary_model, summary_mocked
                        from ai_document_result
                        where user_id = ? and user_file_id = ? and summary_text is not null
                        """,
                (rs, rowNum) -> {
                    StoredDocumentSummary summary = new StoredDocumentSummary();
                    summary.setFilename(rs.getString("filename"));
                    summary.setSummary(rs.getString("summary_text"));
                    summary.setModel(rs.getString("summary_model"));
                    summary.setMocked(rs.getObject("summary_mocked", Boolean.class));
                    return summary;
                },
                userId,
                userFileId
        );
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveSummary(Long userId, Long userFileId, String filename, String summary, String model, Boolean mocked) {
        jdbcTemplate.update(
                """
                        insert into ai_document_result(
                            user_id, user_file_id, filename, summary_text, summary_model, summary_mocked, gmt_modified
                        ) values (?, ?, ?, ?, ?, ?, current_timestamp)
                        on conflict (user_id, user_file_id)
                        do update set filename = excluded.filename,
                                      summary_text = excluded.summary_text,
                                      summary_model = excluded.summary_model,
                                      summary_mocked = excluded.summary_mocked,
                                      gmt_modified = current_timestamp
                        """,
                userId,
                userFileId,
                filename,
                summary,
                model,
                mocked
        );
    }

    @Override
    public StoredDocumentTags getTags(Long userId, Long userFileId) {
        List<StoredDocumentTags> results = jdbcTemplate.query(
                """
                        select filename, tags_json, tags_model, tags_mocked
                        from ai_document_result
                        where user_id = ? and user_file_id = ? and tags_json is not null
                        """,
                (rs, rowNum) -> {
                    StoredDocumentTags tags = new StoredDocumentTags();
                    tags.setFilename(rs.getString("filename"));
                    tags.setTags(readTags(rs.getString("tags_json")));
                    tags.setModel(rs.getString("tags_model"));
                    tags.setMocked(rs.getObject("tags_mocked", Boolean.class));
                    return tags;
                },
                userId,
                userFileId
        );
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveTags(Long userId, Long userFileId, String filename, List<String> tags, String model, Boolean mocked) {
        jdbcTemplate.update(
                """
                        insert into ai_document_result(
                            user_id, user_file_id, filename, tags_json, tags_model, tags_mocked, gmt_modified
                        ) values (?, ?, ?, cast(? as jsonb), ?, ?, current_timestamp)
                        on conflict (user_id, user_file_id)
                        do update set filename = excluded.filename,
                                      tags_json = excluded.tags_json,
                                      tags_model = excluded.tags_model,
                                      tags_mocked = excluded.tags_mocked,
                                      gmt_modified = current_timestamp
                        """,
                userId,
                userFileId,
                filename,
                writeTags(tags),
                model,
                mocked
        );
    }

    @Override
    public void clearDocumentResult(Long userId, Long userFileId) {
        jdbcTemplate.update(
                "delete from ai_document_result where user_id = ? and user_file_id = ?",
                userId,
                userFileId
        );
    }

    private List<String> readTags(String value) {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(value, STRING_LIST_TYPE);
        } catch (Exception e) {
            throw new AiException("Failed to deserialize stored document tags", e, AiErrorCode.DOCUMENT_INDEX_FAILED);
        }
    }

    private String writeTags(List<String> tags) {
        try {
            return objectMapper.writeValueAsString(tags == null ? List.of() : tags);
        } catch (Exception e) {
            throw new AiException("Failed to serialize document tags for persistence", e, AiErrorCode.DOCUMENT_INDEX_FAILED);
        }
    }

    private void initializeSchema() {
        jdbcTemplate.execute("""
                create table if not exists ai_document_result (
                    id bigserial primary key,
                    user_id bigint not null,
                    user_file_id bigint not null,
                    filename varchar(512) not null,
                    summary_text text,
                    summary_model varchar(128),
                    summary_mocked boolean,
                    tags_json jsonb,
                    tags_model varchar(128),
                    tags_mocked boolean,
                    gmt_create timestamp not null default current_timestamp,
                    gmt_modified timestamp not null default current_timestamp,
                    unique (user_id, user_file_id)
                )
                """);
        jdbcTemplate.execute("create index if not exists idx_ai_document_result_owner on ai_document_result(user_id, user_file_id)");
    }
}
