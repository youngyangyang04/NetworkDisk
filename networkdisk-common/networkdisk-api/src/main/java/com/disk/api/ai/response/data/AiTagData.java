package com.disk.api.ai.response.data;

import lombok.Data;

import java.util.List;

@Data
public class AiTagData {

    private Long userId;

    private String fileId;

    private String filename;

    private List<String> tags;

    private String model;

    private Boolean mocked;
}
