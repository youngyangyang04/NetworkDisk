package com.disk.common.exception;

import com.disk.common.base.IErrorCode;

/**
 * 类描述: 断言处理类，用于抛出各种API异常
 *
 * @author weikunkun
 * @date 2024/2/25
 */
public class NetWorkDiskAsserts {

    public static void fail(String message) {
        throw new NetworkDiskException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new NetworkDiskException(errorCode);
    }
}
