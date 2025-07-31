package com.disk.auth.domain.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.disk.api.files.request.UserFileOperateReqest;
import com.disk.api.files.response.UserFileOperateResponse;
import com.disk.api.files.service.UserFileFacadeService;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.api.user.response.UserQueryResponse;
import com.disk.api.user.response.data.UserInfo;
import com.disk.api.user.service.UserFacadeService;
import com.disk.auth.domain.context.LoginContext;
import com.disk.auth.domain.context.RegisterContext;
import com.disk.auth.domain.service.AuthService;
import com.disk.auth.exception.AuthErrorCode;
import com.disk.auth.exception.AuthException;
import com.disk.auth.infrastructure.AuthConstant;
import com.disk.base.constant.BaseConstant;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;


    @DubboReference(version = "1.0.0")
    private UserFileFacadeService userFileFacadeService;

    @Override
    public String login(LoginContext loginContext) {
        //查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginContext.getEmail());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (EmptyUtil.isEmpty(userInfo)) {
            throw new AuthException(AuthErrorCode.USER_NOT_EXIST);
        }
        StpUtil.login(userInfo.getUserId());
        String accessToken = JWTUtil.generateToken(userInfo.getNickName(), BaseConstant.LOGIN_USER_ID, userInfo.getUserId(), AuthConstant.ONE_DAY_TIME_MILLS);
        stringRedisTemplate.opsForValue().set(BaseConstant.USER_LOGIN_PREFIX + userInfo.getUserId(), accessToken);

        return accessToken;
    }

    /**
     * TODO 事物控制 避免出现数据不一致问题
     * user      info      补充用户信息
     * user-file info 补充根文件信息
     * @param registerContext
     * @return
     */
    @Override
    public Long register(RegisterContext registerContext) {
        //注册
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(registerContext.getEmail());
        userRegisterRequest.setPassword(registerContext.getPassword());
        userRegisterRequest.setNickname(registerContext.getNickName());

        UserOperatorResponse<UserInfo> register = userFacadeService.register(userRegisterRequest);
        if (Boolean.FALSE.equals(register.getSuccess())) {
            throw new AuthException(AuthErrorCode.USER_REGISTER_FAIL);
        }
        UserInfo userInfo = register.getData();
        UserFileOperateReqest userFileOperateReqest = new UserFileOperateReqest();
        userFileOperateReqest.setName(BaseConstant.USER_ROOT_FILE);
        userFileOperateReqest.setUserId(userInfo.getUserId());
        userFileOperateReqest.setParentId(BaseConstant.ROOT_PARENT_ID);
        UserFileOperateResponse<Long> userRootFile = userFileFacadeService.createUserRootFile(userFileOperateReqest);
        if (Boolean.FALSE.equals(userRootFile.getSuccess())) {
            throw new AuthException(AuthErrorCode.USER_REGISTER_FAIL);
        }
        return userInfo.getUserId();
    }

    @Override
    public void logout() {
        Long userId = IdUtil.get();
        stringRedisTemplate.delete(BaseConstant.USER_LOGIN_PREFIX + userId);
        StpUtil.logout(userId);
    }
}
