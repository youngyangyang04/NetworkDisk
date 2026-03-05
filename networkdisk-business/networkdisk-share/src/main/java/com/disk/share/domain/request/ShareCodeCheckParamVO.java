package com.disk.share.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareCodeCheckParamVO {

    @NotBlank(message = "shareId is required")
    private String shareId;

    @NotBlank(message = "shareCode is required")
    private String shareCode;
}
