package com.disk.api.ai.request;

import lombok.Data;

@Data
public class AiDocumentTagRequest {

    private Long userId;

    private String fileId;

    private Long userFileId;

    private String filename;

    private Integer topK = 5;
}
