package com.disk.ai.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateDocumentTagsParamVO {

    @NotBlank(message = "fileId can not be blank")
    private String fileId;

    private String filename;

    @Min(value = 1, message = "topK must be greater than 0")
    @Max(value = 20, message = "topK must be less than or equal to 20")
    private Integer topK = 5;
}
