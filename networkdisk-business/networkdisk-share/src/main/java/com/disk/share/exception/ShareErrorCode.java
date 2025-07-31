package com.disk.share.exception;

import com.disk.base.exception.ErrorCode;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public enum ShareErrorCode implements ErrorCode {

    INVALID_ARGS("INVALID_ARGS", "参数非法"),
    SHARE_DAY_TYPE_ERROR("SHARE_DAY_TYPE_ERROR", "分享有效期暂不支持"),
    ;

    private final String code;

    private final String message;

    ShareErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
