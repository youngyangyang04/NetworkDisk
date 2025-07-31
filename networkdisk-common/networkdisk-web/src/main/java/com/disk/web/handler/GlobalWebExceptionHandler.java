package com.disk.web.handler;

import com.disk.base.exception.BizException;
import com.disk.base.exception.SystemException;
import com.disk.web.vo.Result;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.disk.base.response.ResponseCode.ILLEGAL_ARGUMENT;
import static com.disk.base.response.ResponseCode.SYSTEM_ERROR;

/**
 * 类描述: 异常统一处理
 *
 * @author weikunkun
 */
@Slf4j
@ControllerAdvice
public class GlobalWebExceptionHandler {

    /**
     * 未知数据异常
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseBody
    public Result handleUnexpectedTypeException(UnexpectedTypeException ex) {
        log.error("catch UnexpectedTypeException, errorMessage: ", ex);
        return Result.error(ILLEGAL_ARGUMENT.name(), ex.getMessage());
    }


    /**
     * 自定义方法参数校验异常处理器
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result handleValidationExceptions(ConstraintViolationException ex) {
        log.error("ConstraintViolationException occurred.", ex);
        Result result = new Result();
        result.setCode(ILLEGAL_ARGUMENT.name());
        result.setMessage(ex.getMessage());
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义方法参数校验异常处理器
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred.", ex);
        Result result = new Result();
        result.setCode(ILLEGAL_ARGUMENT.name());
        result.setMessage(ex.getMessage());
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义业务异常处理器
     *
     * @param bizException
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result exceptionHandler(BizException bizException) {
        log.error("bizException occurred.", bizException);
        Result result = new Result();
        result.setCode(bizException.getErrorCode().getCode());
        result.setMessage(bizException.getErrorCode().getMessage());
        if (Objects.nonNull(bizException.getMessage())) {
            result.setMessage(bizException.getMessage());
        }
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义系统异常处理器
     *
     * @param systemException
     * @return
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result systemExceptionHandler(SystemException systemException) {
        log.error("systemException occurred.", systemException);
        Result result = new Result();
        result.setCode(systemException.getErrorCode().getCode());
        result.setMessage(systemException.getErrorCode().getMessage());
        result.setSuccess(false);
        return result;
    }

//    /**
//     * 自定义系统异常处理器
//     *
//     * @param throwable
//     * @return
//     */
//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public Result throwableHandler(Throwable throwable) {
//        log.error("throwable occurred.",throwable);
//        Result result = new Result();
//        result.setCode(SYSTEM_ERROR.name());
//        result.setMessage("哎呀，当前网络比较拥挤，请您稍后再试~");
//        result.setSuccess(false);
//        return result;
//    }
}
