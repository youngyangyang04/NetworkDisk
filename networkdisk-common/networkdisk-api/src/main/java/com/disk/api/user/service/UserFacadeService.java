package com.disk.api.user.service;

import com.disk.api.user.request.UserActiveRequest;
import com.disk.api.user.request.UserAuthRequest;
import com.disk.api.user.request.UserModifyRequest;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.api.user.response.UserQueryResponse;
import com.disk.api.user.response.data.UserInfo;

/**
 * 类描述: 用户接口
 *
 * @author weikunkun
 * @date 2024/10/20
 */
public interface UserFacadeService {

    /**
     * 用户信息查询
     *
     * @param request
     * @return
     */
    UserQueryResponse<UserInfo> query(UserQueryRequest request);

    /**
     * 用户注册
     *
     * @param request
     * @return
     */
    UserOperatorResponse<UserInfo> register(UserRegisterRequest request);

    /**
     * 用户修改
     *
     * @param request
     * @return
     */
    UserOperatorResponse modify(UserModifyRequest request);

    /**
     * 用户实名认证
     *
     * @param request
     * @return
     */
    UserOperatorResponse auth(UserAuthRequest request);

    /**
     * 用户激活
     *
     * @param request
     * @return
     */
    UserOperatorResponse active(UserActiveRequest request);

}
