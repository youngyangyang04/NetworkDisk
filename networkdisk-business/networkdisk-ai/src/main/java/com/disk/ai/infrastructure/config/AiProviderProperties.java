package com.disk.ai.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "com.disk.ai.provider")
public class AiProviderProperties {

    private String type = "mock";

    private String baseUrl;

    private String apiKey;

    private String chatPath = "/v1/chat/completions";

    private String embeddingsPath = "/v1/embeddings";

    private String chatModel = "mock-chat-model";

    private String embeddingModel = "mock-embedding-model";

    private Integer embeddingDimension = 768;

    private boolean mockEnabled = true;

    private Integer maxDocumentChars = 120000;

    private Double temperature = 0.2d;

    private Integer maxTokens = 1024;

    private Integer connectTimeoutMillis = 5000;

    private Integer readTimeoutMillis = 60000;
}
