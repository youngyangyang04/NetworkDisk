package com.disk.ai.domain.response;

import lombok.Data;

@Data
public class DocumentSummaryVO {

    private String fileId;

    private String filename;

    private String summary;

    private String model;

    private Boolean mocked;
}
