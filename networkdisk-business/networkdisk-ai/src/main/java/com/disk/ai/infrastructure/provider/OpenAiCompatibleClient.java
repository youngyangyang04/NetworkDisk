package com.disk.ai.infrastructure.provider;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.config.AiProviderProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@ConditionalOnProperty(name = "com.disk.ai.provider.type", havingValue = "openai-compatible")
public class OpenAiCompatibleClient {

    private final AiProviderProperties properties;

    private final RestClient restClient;

    public OpenAiCompatibleClient(AiProviderProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeoutMillis());
        requestFactory.setReadTimeout(properties.getReadTimeoutMillis());

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public ChatResult chatCompletion(String systemPrompt, String userPrompt) {
        validateBaseConfiguration();
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(properties.getChatModel());
        request.setTemperature(properties.getTemperature());
        request.setMaxTokens(properties.getMaxTokens());

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemPrompt));
        messages.add(new ChatMessage("user", userPrompt));
        request.setMessages(messages);

        try {
            ChatCompletionResponse response = restClient.post()
                    .uri(buildUrl(properties.getBaseUrl(), properties.getChatPath()))
                    .headers(headers -> {
                        headers.setBearerAuth(properties.getApiKey());
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    })
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new AiException("Empty chat completion response", AiErrorCode.MODEL_REQUEST_FAILED);
            }

            ChatChoice choice = response.getChoices().get(0);
            String content = choice.getMessage() == null ? null : choice.getMessage().getContent();
            if (StringUtils.isBlank(content)) {
                throw new AiException("The model response content is empty", AiErrorCode.MODEL_REQUEST_FAILED);
            }

            ChatResult result = new ChatResult();
            result.setModel(StringUtils.defaultIfBlank(response.getModel(), properties.getChatModel()));
            result.setContent(content.trim());
            return result;
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            throw new AiException("Failed to request chat completion from the configured AI provider", e, AiErrorCode.MODEL_REQUEST_FAILED);
        }
    }

    public List<float[]> createEmbeddings(List<String> texts) {
        validateBaseConfiguration();
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }

        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel(properties.getEmbeddingModel());
        request.setInput(texts);
        request.setDimensions(properties.getEmbeddingDimension());
        request.setEncodingFormat("float");

        try {
            EmbeddingResponse response = restClient.post()
                    .uri(buildUrl(properties.getBaseUrl(), properties.getEmbeddingsPath()))
                    .headers(headers -> {
                        headers.setBearerAuth(properties.getApiKey());
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    })
                    .body(request)
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                throw new AiException("Empty embeddings response", AiErrorCode.MODEL_REQUEST_FAILED);
            }

            return response.getData().stream()
                    .sorted(Comparator.comparingInt(value -> value.getIndex() == null ? 0 : value.getIndex()))
                    .map(this::toFloatArray)
                    .toList();
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            throw new AiException("Failed to request embeddings from the configured AI provider", e, AiErrorCode.MODEL_REQUEST_FAILED);
        }
    }

    private float[] toFloatArray(EmbeddingData value) {
        if (value.getEmbedding() == null || value.getEmbedding().isEmpty()) {
            throw new AiException("The embedding vector is empty", AiErrorCode.MODEL_REQUEST_FAILED);
        }
        float[] vector = new float[value.getEmbedding().size()];
        for (int i = 0; i < value.getEmbedding().size(); i++) {
            vector[i] = value.getEmbedding().get(i).floatValue();
        }
        return vector;
    }

    private void validateBaseConfiguration() {
        if (StringUtils.isBlank(properties.getBaseUrl())) {
            throw new AiException("com.disk.ai.provider.base-url is required when using openai-compatible mode", AiErrorCode.MODEL_REQUEST_FAILED);
        }
        if (StringUtils.isBlank(properties.getApiKey())) {
            throw new AiException("com.disk.ai.provider.api-key is required when using openai-compatible mode", AiErrorCode.MODEL_REQUEST_FAILED);
        }
    }

    private String buildUrl(String baseUrl, String path) {
        String normalizedBaseUrl = StringUtils.removeEnd(StringUtils.trimToEmpty(baseUrl), "/");
        String normalizedPath = "/" + StringUtils.removeStart(StringUtils.trimToEmpty(path), "/");
        return normalizedBaseUrl + normalizedPath;
    }

    @Data
    public static class ChatResult {

        private String model;

        private String content;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ChatCompletionRequest {

        private String model;

        private List<ChatMessage> messages;

        private Double temperature;

        @JsonProperty("max_tokens")
        private Integer maxTokens;
    }

    @Data
    static class ChatMessage {

        private String role;

        private String content;

        public ChatMessage() {
        }

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ChatCompletionResponse {

        private String model;

        private List<ChatChoice> choices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ChatChoice {

        private ChatMessage message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmbeddingRequest {

        private String model;

        private List<String> input;

        private Integer dimensions;

        @JsonProperty("encoding_format")
        private String encodingFormat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmbeddingResponse {

        private String model;

        private List<EmbeddingData> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmbeddingData {

        private Integer index;

        private List<Double> embedding;
    }
}
