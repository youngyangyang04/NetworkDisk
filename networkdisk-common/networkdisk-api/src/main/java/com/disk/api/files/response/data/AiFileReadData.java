package com.disk.api.files.response.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class AiFileReadData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long userFileId;

    private Long realFileId;

    private String filename;

    private String realPath;

    private String fileSuffix;

    private String filePreviewContentType;

    private String identifier;

    private String fileSize;

    private Integer fileType;
}
