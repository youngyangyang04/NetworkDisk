package com.disk.common.base;

/**
 * API返回码接口
 *
 * @author weikunkun
 * @date 2024/1/14
 */
public interface IErrorCode {
    /**
     * 返回码
     */
    long getCode();

    /**
     * 返回信息
     */
    String getMessage();
}
