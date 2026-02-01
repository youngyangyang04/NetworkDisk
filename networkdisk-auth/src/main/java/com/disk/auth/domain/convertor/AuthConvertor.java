package com.disk.auth.domain.convertor;

import com.disk.auth.domain.context.LoginContext;
import com.disk.auth.domain.context.RegisterContext;
import com.disk.auth.param.LoginParamVO;
import com.disk.auth.param.RegisterParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface AuthConvertor {

    LoginContext loginParamToLoginContext(LoginParamVO loginParamVO);

    RegisterContext registerParamToRegisterContext(RegisterParamVO registerParamVO);
}
