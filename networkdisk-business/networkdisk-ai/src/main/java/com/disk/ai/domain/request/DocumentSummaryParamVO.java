package com.disk.ai.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentSummaryParamVO {

    @NotBlank(message = "fileId can not be blank")
    private String fileId;

    private String filename;

    private String prompt;
}
