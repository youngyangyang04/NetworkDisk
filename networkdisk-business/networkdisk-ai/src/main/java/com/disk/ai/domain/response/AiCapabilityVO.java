package com.disk.ai.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class AiCapabilityVO {

    private String service;

    private String provider;

    private String chatModel;

    private Boolean mockEnabled;

    private Boolean vectorStoreEnabled;

    private Integer embeddingDimension;

    private Integer chunkSize;

    private Integer chunkOverlap;

    private List<String> features;
}
