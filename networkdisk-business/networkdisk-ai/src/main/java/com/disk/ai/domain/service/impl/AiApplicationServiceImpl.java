package com.disk.ai.domain.service.impl;

import com.disk.ai.domain.response.AiCapabilityVO;
import com.disk.ai.domain.service.AiApplicationService;
import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.chunking.ParagraphTextChunker;
import com.disk.ai.infrastructure.chunking.TextChunk;
import com.disk.ai.infrastructure.config.AiIndexProperties;
import com.disk.ai.infrastructure.config.AiProviderProperties;
import com.disk.ai.infrastructure.embedding.EmbeddingClient;
import com.disk.ai.infrastructure.file.AiSourceFile;
import com.disk.ai.infrastructure.file.AiSourceFileLoader;
import com.disk.ai.infrastructure.parser.ParsedDocument;
import com.disk.ai.infrastructure.parser.TikaDocumentParser;
import com.disk.ai.infrastructure.provider.AiProviderClient;
import com.disk.ai.infrastructure.result.DocumentResultStore;
import com.disk.ai.infrastructure.result.StoredDocumentSummary;
import com.disk.ai.infrastructure.result.StoredDocumentTags;
import com.disk.ai.infrastructure.vector.DocumentIndexSummary;
import com.disk.ai.infrastructure.vector.PgVectorDocumentChunk;
import com.disk.ai.infrastructure.vector.PgVectorSearchResult;
import com.disk.ai.infrastructure.vector.VectorStore;
import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiDocumentIndexData;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiApplicationServiceImpl implements AiApplicationService {

    private final AiProviderClient aiProviderClient;

    private final AiProviderProperties aiProviderProperties;

    private final AiIndexProperties aiIndexProperties;

    private final AiSourceFileLoader aiSourceFileLoader;

    private final TikaDocumentParser tikaDocumentParser;

    private final ParagraphTextChunker paragraphTextChunker;

    private final EmbeddingClient embeddingClient;

    private final VectorStore vectorStore;

    private final DocumentResultStore documentResultStore;

    @Override
    public AiCapabilityVO getCapabilities() {
        AiCapabilityVO response = new AiCapabilityVO();
        response.setService("networkdisk-ai");
        response.setProvider(aiProviderProperties.getType());
        response.setChatModel(aiProviderProperties.getChatModel());
        response.setMockEnabled(aiProviderClient.getClass().getSimpleName().contains("Mock"));
        response.setVectorStoreEnabled(vectorStore.isReady());
        response.setEmbeddingDimension(aiProviderProperties.getEmbeddingDimension());
        response.setChunkSize(aiIndexProperties.getChunkSize());
        response.setChunkOverlap(aiIndexProperties.getChunkOverlap());
        response.setFeatures(List.of(
                "file-indexing",
                "document-summary",
                "document-tags",
                "single-file-question",
                "pgvector-retrieval"
        ));
        return response;
    }

    @Override
    public AiDocumentIndexData indexFile(AiDocumentIndexRequest request) {
        if (!vectorStore.isReady()) {
            throw new AiException(AiErrorCode.VECTOR_STORE_DISABLED);
        }

        DocumentIndexSummary existingSummary = vectorStore.getIndexSummary(request.getUserId(), request.getUserFileId());
        if (existingSummary != null && !Boolean.TRUE.equals(request.getForceReindex())) {
            return toIndexData(request, existingSummary, Boolean.TRUE, Boolean.FALSE);
        }

        AiSourceFile sourceFile = aiSourceFileLoader.load(request.getUserId(), request.getFileId(), request.getUserFileId());
        ParsedDocument parsedDocument = tikaDocumentParser.parse(sourceFile);
        List<TextChunk> chunks = paragraphTextChunker.chunk(parsedDocument);
        if (chunks.isEmpty()) {
            throw new AiException(AiErrorCode.DOCUMENT_EMPTY);
        }

        List<float[]> embeddings = embeddingClient.embedAll(chunks.stream().map(TextChunk::getText).toList());
        if (embeddings.size() != chunks.size()) {
            throw new AiException(AiErrorCode.DOCUMENT_INDEX_FAILED);
        }

        List<PgVectorDocumentChunk> vectorChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            PgVectorDocumentChunk vectorChunk = new PgVectorDocumentChunk();
            vectorChunk.setUserId(sourceFile.getUserId());
            vectorChunk.setUserFileId(sourceFile.getUserFileId());
            vectorChunk.setRealFileId(sourceFile.getRealFileId());
            vectorChunk.setFilename(sourceFile.getFilename());
            vectorChunk.setFileSuffix(sourceFile.getFileSuffix());
            vectorChunk.setMediaType(parsedDocument.getMediaType());
            vectorChunk.setParser(parsedDocument.getParser());
            vectorChunk.setBlockIndex(chunk.getBlockIndex());
            vectorChunk.setChunkIndex(chunk.getChunkIndex());
            vectorChunk.setStartOffset(chunk.getStartOffset());
            vectorChunk.setEndOffset(chunk.getEndOffset());
            vectorChunk.setTokenEstimate(chunk.getTokenEstimate());
            vectorChunk.setChunkText(chunk.getText());
            vectorChunk.setEmbedding(embeddings.get(i));
            vectorChunks.add(vectorChunk);
        }
        vectorStore.replaceDocument(sourceFile, parsedDocument, vectorChunks);
        documentResultStore.clearDocumentResult(sourceFile.getUserId(), sourceFile.getUserFileId());

        DocumentIndexSummary summary = new DocumentIndexSummary();
        summary.setFilename(sourceFile.getFilename());
        summary.setMediaType(parsedDocument.getMediaType());
        summary.setParser(parsedDocument.getParser());
        summary.setBlockCount(parsedDocument.getBlocks().size());
        summary.setChunkCount(chunks.size());
        summary.setVectorDimension(embeddingClient.dimension());
        summary.setContentLength((long) sourceFile.getBytes().length);
        return toIndexData(request, summary, Boolean.TRUE, existingSummary != null);
    }

    @Override
    public AiSummaryData summarize(AiDocumentSummaryRequest request) {
        if (canUseStoredSummary(request)) {
            StoredDocumentSummary storedSummary = documentResultStore.getSummary(request.getUserId(), request.getUserFileId());
            if (storedSummary != null && StringUtils.isNotBlank(storedSummary.getSummary())) {
                return buildStoredSummaryResponse(request, storedSummary);
            }
        }

        DocumentPromptContext documentContext = loadDocumentContext(request.getUserId(), request.getFileId(), request.getUserFileId());
        enrichFilename(request, documentContext.filename());
        AiSummaryData response = aiProviderClient.summarize(request, documentContext.content());
        if (canPersistSummary(request, response)) {
            documentResultStore.saveSummary(
                    request.getUserId(),
                    request.getUserFileId(),
                    response.getFilename(),
                    response.getSummary(),
                    response.getModel(),
                    response.getMocked()
            );
        }
        return response;
    }

    @Override
    public AiTagData generateTags(AiDocumentTagRequest request) {
        if (canUseStoredTags(request)) {
            StoredDocumentTags storedTags = documentResultStore.getTags(request.getUserId(), request.getUserFileId());
            if (canSatisfyRequestedTagCount(storedTags, request.getTopK())) {
                return buildStoredTagResponse(request, storedTags);
            }
        }

        DocumentPromptContext documentContext = loadDocumentContext(request.getUserId(), request.getFileId(), request.getUserFileId());
        enrichFilename(request, documentContext.filename());
        AiTagData response = aiProviderClient.generateTags(request, documentContext.content());
        if (canPersistTags(request, response)) {
            documentResultStore.saveTags(
                    request.getUserId(),
                    request.getUserFileId(),
                    response.getFilename(),
                    response.getTags(),
                    response.getModel(),
                    response.getMocked()
            );
        }
        return response;
    }

    @Override
    public AiFileAnswerData answerSingleFileQuestion(AiFileQuestionRequest request) {
        List<PgVectorSearchResult> hits = searchRelevantChunks(request);
        DocumentPromptContext questionContext = resolveQuestionContext(request, hits);
        enrichFilename(request, questionContext.filename());

        AiFileAnswerData data = aiProviderClient.answerSingleFileQuestion(request, questionContext.content());
        if (Boolean.FALSE.equals(request.getIncludeReferences())) {
            data.setReferences(List.of());
            return data;
        }
        if (!hits.isEmpty()) {
            data.setReferences(buildReferences(hits));
        }
        return data;
    }

    private List<PgVectorSearchResult> searchRelevantChunks(AiFileQuestionRequest request) {
        if (!vectorStore.isReady() || request.getUserId() == null || request.getUserFileId() == null) {
            return List.of();
        }
        DocumentIndexSummary summary = vectorStore.getIndexSummary(request.getUserId(), request.getUserFileId());
        if (summary == null) {
            return List.of();
        }
        return vectorStore.search(
                request.getUserId(),
                request.getUserFileId(),
                embeddingClient.embed(request.getQuestion()),
                aiIndexProperties.getRetrievalTopK()
        );
    }

    private DocumentPromptContext resolveQuestionContext(AiFileQuestionRequest request, List<PgVectorSearchResult> hits) {
        if (!hits.isEmpty()) {
            return new DocumentPromptContext(
                    request.getFilename(),
                    mergeWithLimit(hits.stream().map(PgVectorSearchResult::getChunkText).toList())
            );
        }
        return loadDocumentContext(request.getUserId(), request.getFileId(), request.getUserFileId());
    }

    private DocumentPromptContext loadDocumentContext(Long userId, String fileId, Long userFileId) {
        if (vectorStore.isReady() && userId != null && userFileId != null) {
            DocumentIndexSummary summary = vectorStore.getIndexSummary(userId, userFileId);
            if (summary != null) {
                String indexedContent = mergeWithLimit(vectorStore.loadDocumentChunks(userId, userFileId));
                if (StringUtils.isNotBlank(indexedContent)) {
                    return new DocumentPromptContext(summary.getFilename(), indexedContent);
                }
            }
        }

        AiSourceFile sourceFile = aiSourceFileLoader.load(userId, fileId, userFileId);
        ParsedDocument parsedDocument = tikaDocumentParser.parse(sourceFile);
        return new DocumentPromptContext(sourceFile.getFilename(), limitContent(parsedDocument.getPlainText()));
    }

    private void enrichFilename(AiDocumentSummaryRequest request, String filename) {
        if (StringUtils.isBlank(request.getFilename()) && StringUtils.isNotBlank(filename)) {
            request.setFilename(filename);
        }
    }

    private void enrichFilename(AiDocumentTagRequest request, String filename) {
        if (StringUtils.isBlank(request.getFilename()) && StringUtils.isNotBlank(filename)) {
            request.setFilename(filename);
        }
    }

    private void enrichFilename(AiFileQuestionRequest request, String filename) {
        if (StringUtils.isBlank(request.getFilename()) && StringUtils.isNotBlank(filename)) {
            request.setFilename(filename);
        }
    }

    private boolean canUseStoredSummary(AiDocumentSummaryRequest request) {
        return documentResultStore.isReady()
                && request.getUserId() != null
                && request.getUserFileId() != null
                && StringUtils.isBlank(request.getPrompt());
    }

    private boolean canPersistSummary(AiDocumentSummaryRequest request, AiSummaryData response) {
        return canUseStoredSummary(request) && StringUtils.isNotBlank(response.getSummary());
    }

    private AiSummaryData buildStoredSummaryResponse(AiDocumentSummaryRequest request, StoredDocumentSummary storedSummary) {
        AiSummaryData response = new AiSummaryData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(StringUtils.defaultIfBlank(request.getFilename(), storedSummary.getFilename()));
        response.setSummary(storedSummary.getSummary());
        response.setModel(storedSummary.getModel());
        response.setMocked(storedSummary.getMocked());
        return response;
    }

    private boolean canUseStoredTags(AiDocumentTagRequest request) {
        return documentResultStore.isReady()
                && request.getUserId() != null
                && request.getUserFileId() != null;
    }

    private boolean canPersistTags(AiDocumentTagRequest request, AiTagData response) {
        return canUseStoredTags(request) && response.getTags() != null && !response.getTags().isEmpty();
    }

    private boolean canSatisfyRequestedTagCount(StoredDocumentTags storedTags, Integer topK) {
        return storedTags != null
                && storedTags.getTags() != null
                && !storedTags.getTags().isEmpty()
                && storedTags.getTags().size() >= (topK == null ? 5 : topK);
    }

    private AiTagData buildStoredTagResponse(AiDocumentTagRequest request, StoredDocumentTags storedTags) {
        int limit = request.getTopK() == null ? 5 : request.getTopK();
        AiTagData response = new AiTagData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(StringUtils.defaultIfBlank(request.getFilename(), storedTags.getFilename()));
        response.setTags(storedTags.getTags().stream().limit(limit).toList());
        response.setModel(storedTags.getModel());
        response.setMocked(storedTags.getMocked());
        return response;
    }

    private String mergeWithLimit(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        int maxChars = aiProviderProperties.getMaxDocumentChars() == null ? Integer.MAX_VALUE : aiProviderProperties.getMaxDocumentChars();
        for (String text : texts) {
            if (StringUtils.isBlank(text) || builder.length() >= maxChars) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            int remaining = maxChars - builder.length();
            if (text.length() <= remaining) {
                builder.append(text);
            } else {
                builder.append(text, 0, Math.max(0, remaining));
            }
        }
        return builder.toString().trim();
    }

    private String limitContent(String text) {
        String content = StringUtils.defaultString(text);
        int maxChars = aiProviderProperties.getMaxDocumentChars() == null ? Integer.MAX_VALUE : aiProviderProperties.getMaxDocumentChars();
        if (content.length() <= maxChars) {
            return content;
        }
        return content.substring(0, maxChars);
    }

    private List<String> buildReferences(List<PgVectorSearchResult> hits) {
        return hits.stream()
                .map(hit -> "chunk#" + hit.getChunkIndex() + " score=" + String.format("%.4f", hit.getSimilarity())
                        + " text=" + StringUtils.abbreviate(hit.getChunkText(), 120))
                .toList();
    }

    private AiDocumentIndexData toIndexData(AiDocumentIndexRequest request,
                                            DocumentIndexSummary summary,
                                            Boolean indexed,
                                            Boolean reindexed) {
        AiDocumentIndexData data = new AiDocumentIndexData();
        data.setUserId(request.getUserId());
        data.setFileId(request.getFileId());
        data.setUserFileId(request.getUserFileId());
        data.setFilename(summary.getFilename());
        data.setMediaType(summary.getMediaType());
        data.setParser(summary.getParser());
        data.setBlockCount(summary.getBlockCount());
        data.setChunkCount(summary.getChunkCount());
        data.setVectorDimension(summary.getVectorDimension());
        data.setContentLength(summary.getContentLength());
        data.setIndexed(indexed);
        data.setReindexed(reindexed);
        data.setVectorStoreEnabled(vectorStore.isReady());
        return data;
    }

    private record DocumentPromptContext(String filename, String content) {
    }
}
