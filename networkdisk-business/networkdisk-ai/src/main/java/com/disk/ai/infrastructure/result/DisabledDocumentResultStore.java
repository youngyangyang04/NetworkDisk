package com.disk.ai.infrastructure.result;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnMissingBean(DocumentResultStore.class)
public class DisabledDocumentResultStore implements DocumentResultStore {

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public StoredDocumentSummary getSummary(Long userId, Long userFileId) {
        return null;
    }

    @Override
    public void saveSummary(Long userId, Long userFileId, String filename, String summary, String model, Boolean mocked) {
    }

    @Override
    public StoredDocumentTags getTags(Long userId, Long userFileId) {
        return null;
    }

    @Override
    public void saveTags(Long userId, Long userFileId, String filename, List<String> tags, String model, Boolean mocked) {
    }

    @Override
    public void clearDocumentResult(Long userId, Long userFileId) {
    }
}
