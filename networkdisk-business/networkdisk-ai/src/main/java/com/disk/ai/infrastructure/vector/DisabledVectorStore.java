package com.disk.ai.infrastructure.vector;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.file.AiSourceFile;
import com.disk.ai.infrastructure.parser.ParsedDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnMissingBean(VectorStore.class)
public class DisabledVectorStore implements VectorStore {

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public DocumentIndexSummary getIndexSummary(Long userId, Long userFileId) {
        return null;
    }

    @Override
    public List<String> loadDocumentChunks(Long userId, Long userFileId) {
        return Collections.emptyList();
    }

    @Override
    public void replaceDocument(AiSourceFile sourceFile, ParsedDocument parsedDocument, List<PgVectorDocumentChunk> chunks) {
        throw new AiException(AiErrorCode.VECTOR_STORE_DISABLED);
    }

    @Override
    public List<PgVectorSearchResult> search(Long userId, Long userFileId, float[] queryVector, int topK) {
        throw new AiException(AiErrorCode.VECTOR_STORE_DISABLED);
    }
}
