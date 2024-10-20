package com.disk.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.api.user.response.UserQueryResponse;
import com.disk.api.user.response.data.UserInfo;
import com.disk.api.user.service.UserFacadeService;
import com.disk.auth.exception.AuthException;
import com.disk.auth.param.LoginParamVO;
import com.disk.auth.param.RegisterParamVO;
import com.disk.auth.response.LoginResVO;
import com.disk.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.disk.api.notice.constant.NoticeConstant.CAPTCHA_KEY_PREFIX;
import static com.disk.auth.exception.AuthErrorCode.VERIFICATION_CODE_WRONG;
import static com.disk.auth.infrastructure.AuthConstant.DEFAULT_LOGIN_SESSION_TIMEOUT;
import static com.disk.auth.infrastructure.AuthConstant.ROOT_CAPTCHA;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    /**
     * 发送验证码
     */
    @GetMapping("/send-captcha")
    public Result<Boolean> sendCaptcha() {
        // TODO
        return Result.success(true);
    }


    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterParamVO registerParam) {
        //验证码校验
        String cachedCode = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
            throw new AuthException(VERIFICATION_CODE_WRONG);
        }

        //注册
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setTelephone(registerParam.getTelephone());
        userRegisterRequest.setInviteCode(registerParam.getInviteCode());

        UserOperatorResponse registerResult = userFacadeService.register(userRegisterRequest);
        if (registerResult.getSuccess()) {
            return Result.success(true);
        }
        return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
    }


    /**
     * 登陆
     */
    @PostMapping("/login")
    public Result<LoginResVO> login(@Valid @RequestBody LoginParamVO loginParam) {
        if (!ROOT_CAPTCHA.equals(loginParam.getCaptcha())) {
            //验证码校验
            String cachedCode = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + loginParam.getTelephone());
            if (!StringUtils.equalsIgnoreCase(cachedCode, loginParam.getCaptcha())) {
                throw new AuthException(VERIFICATION_CODE_WRONG);
            }
        }

        //判断是注册还是登陆
        //查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            //需要注册
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setTelephone(loginParam.getTelephone());
            userRegisterRequest.setInviteCode(loginParam.getInviteCode());

            UserOperatorResponse response = userFacadeService.register(userRegisterRequest);
            if (response.getSuccess()) {
                userQueryResponse = userFacadeService.query(userQueryRequest);
                userInfo = userQueryResponse.getData();
                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                LoginResVO loginVO = new LoginResVO(userInfo);
                return Result.success(loginVO);
            }

            return Result.error(response.getResponseCode(), response.getResponseMessage());
        } else {
            //登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            LoginResVO loginVO = new LoginResVO(userInfo);
            return Result.success(loginVO);
        }
    }


    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();
        return Result.success(true);
    }
}
