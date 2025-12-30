package com.disk.auth.domain.convertor;

import com.disk.auth.domain.context.LoginContext;
import com.disk.auth.domain.context.RegisterContext;
import com.disk.auth.param.LoginParamVO;
import com.disk.auth.param.RegisterParamVO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-06T15:28:24+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class AuthConvertorImpl implements AuthConvertor {

    @Override
    public LoginContext loginParamToLoginContext(LoginParamVO loginParamVO) {
        if ( loginParamVO == null ) {
            return null;
        }

        LoginContext loginContext = new LoginContext();

        if ( loginParamVO.getCheckCode() != null ) {
            loginContext.setCheckCode( loginParamVO.getCheckCode() );
        }
        if ( loginParamVO.getEmail() != null ) {
            loginContext.setEmail( loginParamVO.getEmail() );
        }
        if ( loginParamVO.getPassword() != null ) {
            loginContext.setPassword( loginParamVO.getPassword() );
        }
        if ( loginParamVO.getNickName() != null ) {
            loginContext.setNickName( loginParamVO.getNickName() );
        }
        if ( loginParamVO.getRememberMe() != null ) {
            loginContext.setRememberMe( loginParamVO.getRememberMe() );
        }

        return loginContext;
    }

    @Override
    public RegisterContext registerParamToRegisterContext(RegisterParamVO registerParamVO) {
        if ( registerParamVO == null ) {
            return null;
        }

        RegisterContext registerContext = new RegisterContext();

        if ( registerParamVO.getCheckCode() != null ) {
            registerContext.setCheckCode( registerParamVO.getCheckCode() );
        }
        if ( registerParamVO.getEmail() != null ) {
            registerContext.setEmail( registerParamVO.getEmail() );
        }
        if ( registerParamVO.getPassword() != null ) {
            registerContext.setPassword( registerParamVO.getPassword() );
        }
        if ( registerParamVO.getNickName() != null ) {
            registerContext.setNickName( registerParamVO.getNickName() );
        }

        return registerContext;
    }
}
