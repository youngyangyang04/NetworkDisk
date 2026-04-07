package com.disk.api.files.request;

import com.disk.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiFileReadRequest extends BaseRequest {

    @NotNull(message = "userId can not be null")
    private Long userId;

    @NotNull(message = "userFileId can not be null")
    private Long userFileId;
}
