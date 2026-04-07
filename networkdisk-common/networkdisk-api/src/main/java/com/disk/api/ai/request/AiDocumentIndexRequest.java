package com.disk.api.ai.request;

import lombok.Data;

@Data
public class AiDocumentIndexRequest {

    private Long userId;

    private String fileId;

    private Long userFileId;

    private String filename;

    private Boolean forceReindex = Boolean.FALSE;
}
