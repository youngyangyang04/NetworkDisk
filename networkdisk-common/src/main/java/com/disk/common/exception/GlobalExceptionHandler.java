package com.disk.common.exception;

import com.disk.common.base.Result;
import com.disk.common.utils.ResultUtil;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLSyntaxErrorException;

/**
 * 类描述: 全局异常处理类
 *
 * @author weikunkun
 * @date 2024/2/25
 */
@ControllerAdvice

public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = NetworkDiskException.class)
    public Result handle(NetworkDiskException e) {
        if (e.getErrorCode() != null) {
            return ResultUtil.failed(e.getErrorCode());
        }
        return ResultUtil.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ResultUtil.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Result handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ResultUtil.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public Result handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        return ResultUtil.failed(message);
    }
}
