package com.disk.common.exception;

import com.disk.common.base.IErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类描述: 网盘异常封装
 *
 * @author weikunkun
 * @date 2024/1/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NetworkDiskException extends RuntimeException {

    private IErrorCode errorCode;

    public NetworkDiskException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public NetworkDiskException(String message) {
        super(message);
    }

    public NetworkDiskException(Throwable cause) {
        super(cause);
    }

    public NetworkDiskException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }

}
