package com.disk.ai.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentIndexParamVO {

    @NotBlank(message = "fileId can not be blank")
    private String fileId;

    private String filename;

    private Boolean forceReindex = Boolean.FALSE;
}
