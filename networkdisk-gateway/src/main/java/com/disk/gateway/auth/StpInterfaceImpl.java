package com.disk.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.disk.api.user.constant.UserPermission;
import com.disk.api.user.constant.UserRole;
import com.disk.api.user.constant.UserStateEnum;
import com.disk.api.user.response.data.UserInfo;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 * @date 2024/10/20
 */
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);
        if (Objects.equals(userInfo.getUserRole(), UserRole.ADMIN) || Objects.equals(userInfo.getState(), UserStateEnum.ACTIVE.name())
            || Objects.equals(userInfo.getState(), UserStateEnum.AUTH.name())) {
            return Lists.newArrayList(UserPermission.BASIC.name(), UserPermission.AUTH.name());
        }

        if (Objects.equals(userInfo.getState(), UserStateEnum.INIT.name())) {
            return Lists.newArrayList(UserPermission.BASIC.name());
        }

        if (Objects.equals(userInfo.getState(), UserStateEnum.FROZEN.name())) {
            return Lists.newArrayList(UserPermission.FROZEN.name());
        }

        return Lists.newArrayList(UserPermission.NONE.name());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);
        if (Objects.equals(UserRole.ADMIN.name(), userInfo.getUserRole())) {
            return Lists.newArrayList(UserRole.ADMIN.name());
        }
        return Lists.newArrayList(UserRole.CUSTOMER.name());
    }
}
