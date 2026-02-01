package com.disk.user.domain.entity.convertor;

import com.disk.api.user.response.data.UserInfo;
import com.disk.user.domain.entity.UserDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-28T19:19:53+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
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
        if ( request.getEmail() != null ) {
            userDO.setEmail( request.getEmail() );
        }
        if ( request.getNickName() != null ) {
            userDO.setNickName( request.getNickName() );
        }
        if ( request.getProfilePhotoUrl() != null ) {
            userDO.setProfilePhotoUrl( request.getProfilePhotoUrl() );
        }

        return userDO;
    }
}
