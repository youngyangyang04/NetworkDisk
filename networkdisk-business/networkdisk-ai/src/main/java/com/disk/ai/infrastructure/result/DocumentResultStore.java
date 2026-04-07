package com.disk.ai.infrastructure.result;

import java.util.List;

public interface DocumentResultStore {

    boolean isReady();

    StoredDocumentSummary getSummary(Long userId, Long userFileId);

    void saveSummary(Long userId, Long userFileId, String filename, String summary, String model, Boolean mocked);

    StoredDocumentTags getTags(Long userId, Long userFileId);

    void saveTags(Long userId, Long userFileId, String filename, List<String> tags, String model, Boolean mocked);

    void clearDocumentResult(Long userId, Long userFileId);
}
