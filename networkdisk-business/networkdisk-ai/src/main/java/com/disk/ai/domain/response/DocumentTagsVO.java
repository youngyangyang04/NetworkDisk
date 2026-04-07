package com.disk.ai.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class DocumentTagsVO {

    private String fileId;

    private String filename;

    private List<String> tags;

    private String model;

    private Boolean mocked;
}
