package com.disk.ai.infrastructure.embedding;

import java.util.List;

public interface EmbeddingClient {

    int dimension();

    float[] embed(String text);

    List<float[]> embedAll(List<String> texts);
}
