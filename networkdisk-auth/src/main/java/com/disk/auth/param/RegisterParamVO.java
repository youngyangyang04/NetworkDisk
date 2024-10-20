package com.disk.auth.param;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Setter
@Getter
public class RegisterParamVO {

    /**
     * 电话
     */
    private String telephone;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 邀请码
     */
    private String inviteCode;
}
