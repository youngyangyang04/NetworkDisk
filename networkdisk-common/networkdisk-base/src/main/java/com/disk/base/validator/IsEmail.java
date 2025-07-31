package com.disk.base.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsEmail {

    String message() default "邮箱格式错误"; // 默认错误信息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
