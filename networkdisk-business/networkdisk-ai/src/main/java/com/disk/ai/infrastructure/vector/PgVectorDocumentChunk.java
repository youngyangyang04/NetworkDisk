package com.disk.ai.infrastructure.vector;

import lombok.Data;

@Data
public class PgVectorDocumentChunk {

    private Long userId;

    private Long userFileId;

    private Long realFileId;

    private String filename;

    private String fileSuffix;

    private String mediaType;

    private String parser;

    private Integer blockIndex;

    private Integer chunkIndex;

    private Integer startOffset;

    private Integer endOffset;

    private Integer tokenEstimate;

    private String chunkText;

    private float[] embedding;
}
