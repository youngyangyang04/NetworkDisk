package com.disk.ai.infrastructure.result;

import lombok.Data;

import java.util.List;

@Data
public class StoredDocumentTags {

    private String filename;

    private List<String> tags;

    private String model;

    private Boolean mocked;
}
