package com.disk.recycle.exception;

import com.disk.base.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@AllArgsConstructor
public enum RecycleErrorCode implements ErrorCode {
    ;

    private final String code;

    private final String message;

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
