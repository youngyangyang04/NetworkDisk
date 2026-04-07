package com.disk.ai.infrastructure.vector;

import com.disk.ai.infrastructure.file.AiSourceFile;
import com.disk.ai.infrastructure.parser.ParsedDocument;

import java.util.List;

public interface VectorStore {

    boolean isReady();

    DocumentIndexSummary getIndexSummary(Long userId, Long userFileId);

    List<String> loadDocumentChunks(Long userId, Long userFileId);

    void replaceDocument(AiSourceFile sourceFile,
                         ParsedDocument parsedDocument,
                         List<PgVectorDocumentChunk> chunks);

    List<PgVectorSearchResult> search(Long userId, Long userFileId, float[] queryVector, int topK);
}
