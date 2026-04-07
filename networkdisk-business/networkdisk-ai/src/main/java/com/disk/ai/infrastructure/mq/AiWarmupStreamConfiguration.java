package com.disk.ai.infrastructure.mq;

import com.disk.ai.domain.service.AiApplicationService;
import com.disk.api.ai.message.AiDocumentWarmupMessage;
import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.mq.param.MessageBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiWarmupStreamConfiguration {

    private final ObjectMapper objectMapper;

    private final AiApplicationService aiApplicationService;

    @Bean("aiWarmupConsumer")
    public Consumer<Message<?>> aiWarmupConsumer() {
        return message -> {
            AiDocumentWarmupMessage warmupMessage = readWarmupMessage(message);
            if (warmupMessage == null || warmupMessage.getUserId() == null || warmupMessage.getUserFileId() == null) {
                log.warn("ignore invalid ai warmup message: {}", message == null ? null : message.getPayload());
                return;
            }

            log.info("consume ai warmup message, userId={}, userFileId={}",
                    warmupMessage.getUserId(), warmupMessage.getUserFileId());

            AiDocumentIndexRequest indexRequest = new AiDocumentIndexRequest();
            indexRequest.setUserId(warmupMessage.getUserId());
            indexRequest.setUserFileId(warmupMessage.getUserFileId());
            indexRequest.setFileId(warmupMessage.getFileId());
            indexRequest.setFilename(warmupMessage.getFilename());
            indexRequest.setForceReindex(Boolean.FALSE);
            aiApplicationService.indexFile(indexRequest);

            AiDocumentSummaryRequest summaryRequest = new AiDocumentSummaryRequest();
            summaryRequest.setUserId(warmupMessage.getUserId());
            summaryRequest.setUserFileId(warmupMessage.getUserFileId());
            summaryRequest.setFileId(warmupMessage.getFileId());
            summaryRequest.setFilename(warmupMessage.getFilename());
            aiApplicationService.summarize(summaryRequest);

            AiDocumentTagRequest tagRequest = new AiDocumentTagRequest();
            tagRequest.setUserId(warmupMessage.getUserId());
            tagRequest.setUserFileId(warmupMessage.getUserFileId());
            tagRequest.setFileId(warmupMessage.getFileId());
            tagRequest.setFilename(warmupMessage.getFilename());
            tagRequest.setTopK(warmupMessage.getTopK());
            aiApplicationService.generateTags(tagRequest);
        };
    }

    private AiDocumentWarmupMessage readWarmupMessage(Message<?> message) {
        if (message == null || message.getPayload() == null) {
            return null;
        }

        MessageBody messageBody = objectMapper.convertValue(message.getPayload(), MessageBody.class);
        if (messageBody == null || StringUtils.isBlank(messageBody.getBody())) {
            return null;
        }

        try {
            return objectMapper.readValue(messageBody.getBody(), AiDocumentWarmupMessage.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize ai warmup message body", e);
        }
    }
}
