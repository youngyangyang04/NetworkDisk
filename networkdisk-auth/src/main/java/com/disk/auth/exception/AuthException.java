package com.disk.auth.exception;

import com.disk.base.exception.BizException;
import com.disk.base.exception.ErrorCode;

/**
 * 类描述: AUTH 模块异常
 *
 * @author weikunkun
 */
public class AuthException extends BizException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public AuthException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
