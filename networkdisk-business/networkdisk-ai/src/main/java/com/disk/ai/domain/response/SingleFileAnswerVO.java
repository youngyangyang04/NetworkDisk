package com.disk.ai.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class SingleFileAnswerVO {

    private String fileId;

    private String filename;

    private String question;

    private String answer;

    private List<String> references;

    private String model;

    private Boolean mocked;
}
