package com.disk.ai.infrastructure.result;

import lombok.Data;

@Data
public class StoredDocumentSummary {

    private String filename;

    private String summary;

    private String model;

    private Boolean mocked;
}
