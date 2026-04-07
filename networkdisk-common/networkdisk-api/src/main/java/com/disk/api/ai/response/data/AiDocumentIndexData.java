package com.disk.api.ai.response.data;

import lombok.Data;

@Data
public class AiDocumentIndexData {

    private Long userId;

    private String fileId;

    private Long userFileId;

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
