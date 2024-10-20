package com.disk.base.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author weikunkun
 */
@Setter
@Getter
public class SingleResponse<T> extends BaseResponse {
    private static final long serialVersionUID = 1L;

    private T data;

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(data);
        return singleResponse;
    }

    public static <T> SingleResponse<T> fail(String errorCode, String errorMessage) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(false);
        singleResponse.setResponseCode(errorCode);
        singleResponse.setResponseMessage(errorMessage);
        return singleResponse;
    }

}
