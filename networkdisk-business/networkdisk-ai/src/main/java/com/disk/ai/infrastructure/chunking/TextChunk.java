package com.disk.ai.infrastructure.chunking;

import lombok.Data;

@Data
public class TextChunk {

    private Integer chunkIndex;

    private Integer blockIndex;

    private String text;

    private Integer startOffset;

    private Integer endOffset;

    private Integer tokenEstimate;
}
