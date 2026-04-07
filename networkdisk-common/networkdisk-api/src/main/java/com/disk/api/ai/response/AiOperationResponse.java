package com.disk.api.ai.response;

import com.disk.base.response.BaseResponse;
import com.disk.base.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiOperationResponse<T> extends BaseResponse {

    private T data;

    public static <T> AiOperationResponse<T> success(T data) {
        AiOperationResponse<T> response = new AiOperationResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setResponseCode(ResponseCode.SUCCESS.name());
        response.setResponseMessage("OK");
        response.setData(data);
        return response;
    }
}
