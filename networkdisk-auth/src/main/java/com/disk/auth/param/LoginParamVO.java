package com.disk.auth.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class LoginParamVO {

    /**
     * Captcha code (reserved).
     */
    private String checkCode;

    /**
     * User email.
     */
    @NotBlank(message = "email cannot be blank")
    @Email(message = "email format is invalid")
    private String email;

    /**
     * Plain password.
     */
    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max = 64, message = "password length must be between 6 and 64")
    private String password;

    /**
     * Remember me.
     */
    private Boolean rememberMe;
}
