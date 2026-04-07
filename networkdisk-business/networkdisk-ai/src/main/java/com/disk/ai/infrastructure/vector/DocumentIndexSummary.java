package com.disk.ai.infrastructure.vector;

import lombok.Data;

@Data
public class DocumentIndexSummary {

    private String filename;

    private String mediaType;

    private String parser;

    private Integer blockCount;

    private Integer chunkCount;

    private Integer vectorDimension;

    private Long contentLength;
}
