package com.disk.user.domain.entity.convertor;

import com.disk.api.user.response.data.UserInfo;
import com.disk.user.domain.entity.UserDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-16T22:02:14+0800",
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
        if ( request.getEmail() != null ) {
            userInfo.setEmail( request.getEmail() );
        }
        if ( request.getProfilePhotoUrl() != null ) {
            userInfo.setProfilePhotoUrl( request.getProfilePhotoUrl() );
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
        if ( request.getEmail() != null ) {
            userDO.setEmail( request.getEmail() );
        }
        if ( request.getProfilePhotoUrl() != null ) {
            userDO.setProfilePhotoUrl( request.getProfilePhotoUrl() );
        }

        return userDO;
    }
}
