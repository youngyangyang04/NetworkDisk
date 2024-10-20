package com.disk.base.exception;

/**
 * 错误码
 *
 * @author weikunkun
 */
public enum RepoErrorCode implements ErrorCode {
    /**
     * 未知错误
     */
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),

    /**
     * 数据库插入失败
     */
    INSERT_FAILED("INSERT_FAILED", "数据库插入失败"),

    /**
     * 数据库更新失败
     */
    UPDATE_FAILED("UPDATE_FAILED", "数据库更新失败"),
    ;

    private String code;

    private String message;

    RepoErrorCode(String code, String message) {
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
