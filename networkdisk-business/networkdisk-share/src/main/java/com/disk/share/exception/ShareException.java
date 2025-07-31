package com.disk.share.exception;

import com.disk.base.exception.BizException;
import com.disk.base.exception.ErrorCode;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class ShareException extends BizException {

    public ShareException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ShareException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ShareException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ShareException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ShareException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
