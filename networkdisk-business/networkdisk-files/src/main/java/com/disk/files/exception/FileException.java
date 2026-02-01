package com.disk.files.exception;

import com.disk.base.exception.BizException;
import com.disk.base.exception.ErrorCode;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class FileException extends BizException {

    public FileException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public FileException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public FileException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public FileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
