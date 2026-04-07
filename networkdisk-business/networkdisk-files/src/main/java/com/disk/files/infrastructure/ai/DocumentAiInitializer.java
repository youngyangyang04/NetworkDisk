package com.disk.files.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.disk.api.ai.message.AiDocumentWarmupMessage;
import com.disk.base.utils.FileUtil;
import com.disk.base.utils.IdUtil;
import com.disk.mq.producer.StreamProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentAiInitializer {

    private static final String AI_WARMUP_BINDING = "aiWarmup-out-0";

    private static final String AI_WARMUP_TAG = "document-ai-warmup";

    private static final Set<String> SUPPORTED_SUFFIXES = Set.of(
            ".pdf",
            ".doc",
            ".docx",
            ".txt",
            ".md",
            ".markdown",
            ".csv",
            ".xls",
            ".xlsx",
            ".ppt",
            ".pptx",
            ".html",
            ".htm",
            ".xml",
            ".json",
            ".sql",
            ".java",
            ".js",
            ".css"
    );

    private final StreamProducer streamProducer;

    private final ObjectMapper objectMapper;

    public void scheduleInitialize(Long userId, Long userFileId, String filename) {
        if (userId == null || userFileId == null || !supports(filename)) {
            return;
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishWarmupMessage(userId, userFileId, filename);
                }
            });
            return;
        }
        publishWarmupMessage(userId, userFileId, filename);
    }

    private void publishWarmupMessage(Long userId, Long userFileId, String filename) {
        try {
            AiDocumentWarmupMessage message = new AiDocumentWarmupMessage();
            message.setUserId(userId);
            message.setUserFileId(userFileId);
            message.setFileId(IdUtil.encrypt(userFileId));
            message.setFilename(filename);

            boolean sent = streamProducer.send(
                    AI_WARMUP_BINDING,
                    AI_WARMUP_TAG,
                    objectMapper.writeValueAsString(message)
            );
            if (!sent) {
                log.warn("publish document ai warmup message returned false, userId={}, userFileId={}", userId, userFileId);
            }
        } catch (Exception e) {
            log.warn("publish document ai warmup message failed, userId={}, userFileId={}", userId, userFileId, e);
        }
    }

    private boolean supports(String filename) {
        String fileSuffix = StringUtils.lowerCase(StringUtils.trimToEmpty(FileUtil.getFileSuffix(filename)), Locale.ROOT);
        return SUPPORTED_SUFFIXES.contains(fileSuffix);
    }
}
