package com.disk.auth.exception;

import com.disk.base.exception.ErrorCode;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public enum AuthErrorCode implements ErrorCode {

    /**
     * 用户状态不可用
     */
    USER_STATUS_IS_NOT_ACTIVE("USER_STATUS_IS_NOT_ACTIVE", "用户状态不可用"),

    /**
     * 验证码错误
     */
    VERIFICATION_CODE_WRONG("VERIFICATION_CODE_WRONG", "验证码错误"),

    /**
     * 用户信息查询失败
     */
    USER_QUERY_FAILED("USER_QUERY_FAILED", "用户信息查询失败"),

    /**
     * 用户未登录
     */
    USER_NOT_LOGIN("USER_NOT_LOGIN", "用户未登录"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST("USER_NOT_EXIST", "用户不存在");
    ;

    private final String code;

    private final String message;

    AuthErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
