package com.disk.api.ai.message;

import lombok.Data;

@Data
public class AiDocumentWarmupMessage {

    private Long userId;

    private Long userFileId;

    private String fileId;

    private String filename;

    private Integer topK = 5;
}
