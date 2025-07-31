package com.disk.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.disk.auth.domain.context.LoginContext;
import com.disk.auth.domain.context.RegisterContext;
import com.disk.auth.domain.convertor.AuthConvertor;
import com.disk.auth.domain.service.AuthService;
import com.disk.auth.infrastructure.AuthConstant;
import com.disk.auth.param.LoginParamVO;
import com.disk.auth.param.RegisterParamVO;
import com.disk.base.utils.IdUtil;
import com.disk.web.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthConvertor authConvertor;



    /**
     * 发送验证码
     */
    @GetMapping("/send-captcha")
    public void sendCaptcha(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") Integer type) throws IOException {
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 50, AuthConstant.CAPTCHA_LENGTH, 3);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", AuthConstant.CAPTCHA_EXPIRES);
        response.setContentType("image/jpeg");
        shearCaptcha.write(response.getOutputStream());
        if (type == null || type.equals(0)) {
            request.getSession().setAttribute(AuthConstant.CAPTCHA_VERIFY_LOGIN, shearCaptcha.getCode());
            return;
        }
        request.getSession().setAttribute(AuthConstant.CAPTCHA_VERIFY_EMAIL, shearCaptcha.getCode());
    }


    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterParamVO registerParam) {
        //TODO 验证码校验
        RegisterContext registerContext = authConvertor.registerParamToRegisterContext(registerParam);
        Long userId = authService.register(registerContext);
        return Result.success(IdUtil.encrypt(userId));
    }


    /**
     * 登陆
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginParamVO loginParam) {
        //TODO 验证码校验
        LoginContext loginContext = authConvertor.loginParamToLoginContext(loginParam);
        String loginToken = authService.login(loginContext);
        //登录
        return Result.success(loginToken);
    }


    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        // TODO 改为sa-token方式
        authService.logout();
        return Result.success(true);
    }
}
