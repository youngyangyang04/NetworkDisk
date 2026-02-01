package com.disk.gateway.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.disk.api.user.constant.UserPermission;
import com.disk.api.user.constant.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 * @date 2024/10/20
 */
@Configuration
public class SaTokenConfigure {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/auth/login 用于开放登录
                    SaRouter.match("/**").notMatch("/auth/**").check(r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
                    SaRouter.match("/admin/**", r -> StpUtil.checkRole(UserRole.ADMIN.name()));
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(this::getSaResult);
    }

    private SaResult getSaResult(Throwable throwable) {
        switch (throwable) {
            case NotLoginException notLoginException:
                return SaResult.error("请先登录");
            case NotRoleException notRoleException:
                if (UserRole.ADMIN.name().equals(notRoleException.getRole())) {
                    return SaResult.error("请勿越权使用！");
                }
                return SaResult.error("您无权限进行此操作！");
            case NotPermissionException notPermissionException:
                if (UserPermission.AUTH.name().equals(notPermissionException.getPermission())) {
                    return SaResult.error("请先完成实名认证！");
                }
                return SaResult.error("您无权限进行此操作！");
            default:
                return SaResult.error(throwable.getMessage());
        }
    }
}
