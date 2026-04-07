package com.disk.ai.infrastructure.embedding;

import com.disk.ai.infrastructure.config.AiProviderProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@ConditionalOnMissingBean(EmbeddingClient.class)
public class MockEmbeddingClient implements EmbeddingClient {

    private final AiProviderProperties aiProviderProperties;

    public MockEmbeddingClient(AiProviderProperties aiProviderProperties) {
        this.aiProviderProperties = aiProviderProperties;
    }

    @Override
    public int dimension() {
        return aiProviderProperties.getEmbeddingDimension();
    }

    @Override
    public float[] embed(String text) {
        int dimension = dimension();
        float[] vector = new float[dimension];
        byte[] bytes = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length == 0) {
            vector[0] = 1.0f;
            return vector;
        }
        for (int i = 0; i < bytes.length; i++) {
            int bucket = i % dimension;
            vector[bucket] += (bytes[i] & 0xFF) / 255.0f;
        }
        normalize(vector);
        return vector;
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        return texts.stream().map(this::embed).toList();
    }

    private void normalize(float[] vector) {
        double sum = 0.0d;
        for (float value : vector) {
            sum += value * value;
        }
        double norm = Math.sqrt(sum);
        if (norm == 0.0d) {
            vector[0] = 1.0f;
            return;
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / norm);
        }
    }
}
