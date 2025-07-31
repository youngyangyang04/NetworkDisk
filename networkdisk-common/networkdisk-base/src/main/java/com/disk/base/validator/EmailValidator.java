package com.disk.base.validator;

import cn.hutool.core.lang.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class EmailValidator implements ConstraintValidator<IsEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return Validator.isEmail(email);
    }
}
