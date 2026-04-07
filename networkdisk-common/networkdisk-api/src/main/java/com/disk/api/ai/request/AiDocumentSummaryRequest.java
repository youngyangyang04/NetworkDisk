package com.disk.api.ai.request;

import lombok.Data;

@Data
public class AiDocumentSummaryRequest {

    private Long userId;

    private String fileId;

    private Long userFileId;

    private String filename;

    private String prompt;
}
