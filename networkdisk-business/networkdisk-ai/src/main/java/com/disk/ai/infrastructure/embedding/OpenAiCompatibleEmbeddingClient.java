package com.disk.ai.infrastructure.embedding;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.config.AiProviderProperties;
import com.disk.ai.infrastructure.provider.OpenAiCompatibleClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "com.disk.ai.provider.type", havingValue = "openai-compatible")
public class OpenAiCompatibleEmbeddingClient implements EmbeddingClient {

    private final OpenAiCompatibleClient openAiCompatibleClient;

    private final AiProviderProperties properties;

    @Override
    public int dimension() {
        return properties.getEmbeddingDimension();
    }

    @Override
    public float[] embed(String text) {
        List<float[]> results = embedAll(List.of(text));
        return results.isEmpty() ? new float[dimension()] : results.get(0);
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }
        return openAiCompatibleClient.createEmbeddings(texts).stream()
                .map(this::validateDimension)
                .toList();
    }

    private float[] validateDimension(float[] vector) {
        if (vector.length != dimension()) {
            throw new AiException(
                    "Configured embedding dimension " + dimension() + " does not match provider response dimension " + vector.length,
                    AiErrorCode.MODEL_REQUEST_FAILED
            );
        }
        return vector;
    }
}
