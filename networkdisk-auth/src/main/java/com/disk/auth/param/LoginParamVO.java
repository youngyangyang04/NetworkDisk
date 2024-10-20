package com.disk.auth.param;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class LoginParamVO extends RegisterParamVO {

    /**
     * 记住我
     */
    private Boolean rememberMe;


}
