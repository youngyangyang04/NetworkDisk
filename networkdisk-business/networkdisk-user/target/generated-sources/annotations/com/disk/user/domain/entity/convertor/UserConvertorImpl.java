package com.disk.user.domain.entity.convertor;

import com.disk.api.user.constant.UserStateEnum;
import com.disk.api.user.response.data.UserInfo;
import com.disk.user.domain.entity.UserDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-01T20:57:55+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
public class UserConvertorImpl implements UserConvertor {

    @Override
    public UserInfo mapToVo(UserDO request) {
        if ( request == null ) {
            return null;
        }

        UserInfo userInfo = new UserInfo();

        if ( request.getId() != null ) {
            userInfo.setUserId( request.getId() );
        }
        if ( request.getNickName() != null ) {
            userInfo.setNickName( request.getNickName() );
        }
        if ( request.getTelephone() != null ) {
            userInfo.setTelephone( request.getTelephone() );
        }
        if ( request.getState() != null ) {
            userInfo.setState( request.getState().name() );
        }
        if ( request.getProfilePhotoUrl() != null ) {
            userInfo.setProfilePhotoUrl( request.getProfilePhotoUrl() );
        }
        if ( request.getCertification() != null ) {
            userInfo.setCertification( request.getCertification() );
        }
        if ( request.getUserRole() != null ) {
            userInfo.setUserRole( request.getUserRole() );
        }

        return userInfo;
    }

    @Override
    public UserDO mapToEntity(UserInfo request) {
        if ( request == null ) {
            return null;
        }

        UserDO userDO = new UserDO();

        if ( request.getUserId() != null ) {
            userDO.setId( request.getUserId() );
        }
        if ( request.getNickName() != null ) {
            userDO.setNickName( request.getNickName() );
        }
        if ( request.getState() != null ) {
            userDO.setState( Enum.valueOf( UserStateEnum.class, request.getState() ) );
        }
        if ( request.getTelephone() != null ) {
            userDO.setTelephone( request.getTelephone() );
        }
        if ( request.getProfilePhotoUrl() != null ) {
            userDO.setProfilePhotoUrl( request.getProfilePhotoUrl() );
        }
        if ( request.getCertification() != null ) {
            userDO.setCertification( request.getCertification() );
        }
        if ( request.getUserRole() != null ) {
            userDO.setUserRole( request.getUserRole() );
        }

        return userDO;
    }
}
