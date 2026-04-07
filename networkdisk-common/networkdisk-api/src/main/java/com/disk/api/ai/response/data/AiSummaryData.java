package com.disk.api.ai.response.data;

import lombok.Data;

@Data
public class AiSummaryData {

    private Long userId;

    private String fileId;

    private String filename;

    private String summary;

    private String model;

    private Boolean mocked;
}
