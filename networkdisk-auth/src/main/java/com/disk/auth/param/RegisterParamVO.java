package com.disk.auth.param;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
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
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱验证吗
     */
    private String emailCode;

}
