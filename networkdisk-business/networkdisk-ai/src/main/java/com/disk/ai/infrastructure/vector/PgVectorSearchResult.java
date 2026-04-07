package com.disk.ai.infrastructure.vector;

import lombok.Data;

@Data
public class PgVectorSearchResult {

    private Integer chunkIndex;

    private Integer blockIndex;

    private String chunkText;

    private Double similarity;
}
