package com.disk.auth.domain.service;

import com.disk.api.user.response.UserOperatorResponse;
import com.disk.auth.domain.context.LoginContext;
import com.disk.auth.domain.context.RegisterContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface AuthService {

    String login(LoginContext loginContext);

    Long register(RegisterContext registerContext);

    void logout();
}
