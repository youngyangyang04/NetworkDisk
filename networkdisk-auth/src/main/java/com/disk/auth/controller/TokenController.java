package com.disk.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.disk.auth.exception.AuthErrorCode;
import com.disk.auth.exception.AuthException;
import com.disk.auth.infrastructure.AuthConstant;
import com.disk.cache.constant.CacheConstant;
import com.disk.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.disk.auth.infrastructure.AuthConstant.TOKEN_PREFIX;
import static com.disk.cache.constant.CacheConstant.CACHE_KEY_SEPARATOR;

/**
 * 类描述: Token
 *
 * @author weikunkun
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("token")
public class TokenController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/get")
    public Result<String> getToken(@NotBlank String scene) {
        if (StpUtil.isLogin()) {
            String token = UUID.randomUUID().toString();
            String tokenKey = TOKEN_PREFIX + scene + CACHE_KEY_SEPARATOR + token;
            redisTemplate.opsForValue().set(tokenKey, token, 30, TimeUnit.MINUTES);
            return Result.success(tokenKey);
        }
        throw new AuthException(AuthErrorCode.USER_NOT_LOGIN);
    }





}
