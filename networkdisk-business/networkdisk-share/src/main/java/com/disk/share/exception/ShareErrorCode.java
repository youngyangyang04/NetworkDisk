package com.disk.share.exception;

import com.disk.base.exception.ErrorCode;

/**
 * Share module error codes.
 */
public enum ShareErrorCode implements ErrorCode {

    INVALID_ARGS("INVALID_ARGS", "Invalid arguments"),
    SHARE_DAY_TYPE_ERROR("SHARE_DAY_TYPE_ERROR", "Unsupported share day type"),
    SHARE_NOT_FOUND("SHARE_NOT_FOUND", "Share not found"),
    SHARE_EXPIRED("SHARE_EXPIRED", "Share has expired"),
    SHARE_CODE_ERROR("SHARE_CODE_ERROR", "Invalid share code"),
    SHARE_FILE_NOT_FOUND("SHARE_FILE_NOT_FOUND", "Shared file not found");

    private final String code;

    private final String message;

    ShareErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
