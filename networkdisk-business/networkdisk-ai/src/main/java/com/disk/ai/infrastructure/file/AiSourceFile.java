package com.disk.ai.infrastructure.file;

import lombok.Data;

@Data
public class AiSourceFile {

    private Long userId;

    private String fileId;

    private Long userFileId;

    private Long realFileId;

    private String filename;

    private String fileSuffix;

    private String contentType;

    private String identifier;

    private String fileSize;

    private Integer fileType;

    private byte[] bytes;
}
