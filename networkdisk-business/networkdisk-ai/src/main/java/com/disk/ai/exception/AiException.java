package com.disk.ai.exception;

import com.disk.base.exception.ErrorCode;
import com.disk.base.exception.SystemException;

public class AiException extends SystemException {

    public AiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AiException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AiException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
