package com.disk.api.ai.request;

import lombok.Data;

@Data
public class AiFileQuestionRequest {

    private Long userId;

    private String fileId;

    private Long userFileId;

    private String filename;

    private String question;

    private Boolean includeReferences = Boolean.TRUE;
}
