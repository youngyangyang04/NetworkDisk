package com.disk.api.ai.response.data;

import lombok.Data;

import java.util.List;

@Data
public class AiFileAnswerData {

    private Long userId;

    private String fileId;

    private String filename;

    private String question;

    private String answer;

    private List<String> references;

    private String model;

    private Boolean mocked;
}
