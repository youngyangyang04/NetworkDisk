package com.disk.auth.domain.context;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class LoginContext extends RegisterContext{

    /**
     * 记住我
     */
    private boolean rememberMe;
}
