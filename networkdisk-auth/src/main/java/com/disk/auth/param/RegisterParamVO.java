package com.disk.auth.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Register request payload.
 */
@Setter
@Getter
public class RegisterParamVO {

    /**
     * 验证码
     */
    private String checkCode;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不可为空")
    @Email(message = "邮箱格式有误")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需要为6-64位")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不可为空")
    @Size(min = 3, message = "用户名长度不能少于3位")
    private String nickName;

    /**
     * 邮箱验证码
     */
    private String emailCode;

}
