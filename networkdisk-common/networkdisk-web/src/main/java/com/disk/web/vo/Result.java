package com.disk.web.vo;

import com.disk.base.exception.ErrorCode;
import com.disk.base.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;

import static com.disk.base.response.ResponseCode.SUCCESS;


/**
 * @author weikunkun
 */
@Getter
@Setter
public class Result<T> {
    /**
     * 状态吗
     */
    private String code;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 数据，可以是任何类型的VO
     */
    private T data;

    public Result() {
    }

    public Result(Boolean success, String code) {
        this.success = success;
        this.code = code;
    }

    public Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(Boolean success, String code, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public Result(SingleResponse<T> singleResponse){
        this.success = singleResponse.getSuccess();
        this.data = singleResponse.getData();
        this.code = singleResponse.getResponseCode();
        this.message = singleResponse.getResponseMessage();
    }

    public static <T> Result<T> success() {
        return new Result<>(true, SUCCESS.name());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, SUCCESS.name(), SUCCESS.name(), data);
    }

    public static <T> Result<T> error(String errorCode,String errorMsg) {
        return new Result<>(false, errorCode, errorMsg, null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
}
