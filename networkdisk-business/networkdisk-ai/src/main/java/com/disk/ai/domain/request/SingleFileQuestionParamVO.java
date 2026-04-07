package com.disk.ai.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SingleFileQuestionParamVO {

    @NotBlank(message = "fileId can not be blank")
    private String fileId;

    private String filename;

    @NotBlank(message = "question can not be blank")
    private String question;

    private Boolean includeReferences = Boolean.TRUE;
}
