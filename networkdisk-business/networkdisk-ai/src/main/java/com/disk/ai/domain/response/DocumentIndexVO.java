package com.disk.ai.domain.response;

import lombok.Data;

@Data
public class DocumentIndexVO {

    private String fileId;

    private String filename;

    private String mediaType;

    private String parser;

    private Integer blockCount;

    private Integer chunkCount;

    private Integer vectorDimension;

    private Long contentLength;

    private Boolean indexed;

    private Boolean reindexed;

    private Boolean vectorStoreEnabled;
}
