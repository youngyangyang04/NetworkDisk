package com.disk.web.aspect;

import com.disk.base.constant.BaseConstant;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.JWTUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.web.annotation.LoginIgnore;
import com.disk.web.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.disk.base.exception.BizErrorCode.NOT_LOGIN_ERROR;

/**
 * 类描述: 用户登录信息拦截器
 *
 * @author weikunkun
 */
@Slf4j
@Aspect
@Component
public class UserInfoLoginAspect {

    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * 切点表达式
     * 表示切面会拦截 com.tangrl.pan.server.modules 包下所有 controller 的方法。
     */
    private final static String POINT_CUT = "execution(* com.disk.*.controller..*(..))";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 切点模版方法
     * 定义了一个切点，名称为 loginAuth。
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * 1、需要判断需不需要校验登录信息
     * 2、校验登录信息：
     * a、获取token 从请求头或者参数
     * b、从缓存中获取token，进行比对
     * c、解析token
     * d、解析的userId存入线程上下文，供下游使用
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    // 定义了环绕增强逻辑，围绕 loginAuth() 切点执行
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 判断是否需要校验登录信息，如果需要，则执行登录校验逻辑
        // 没有 @LoginIgnore 注解的方法会进行登陆校验
        if (checkNeedCheckLoginInfo(proceedingJoinPoint)) {
            // 获取当前请求的 HttpServletRequest 对象
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            // 从请求头或请求参数中获取 accessToken，并进行校验
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("login pre intercept，uri：[{}]", requestURI);
            // 如果校验通过，将 userId 存入线程上下文
            if (!checkAndSaveUserId(request)) {
                log.warn("成功拦截到请求，URI为：{}. 检测到用户未登录，将跳转至登录页面", requestURI);
                return Result.error(NOT_LOGIN_ERROR.getCode(), NOT_LOGIN_ERROR.getMessage());
            }
            log.info("login pre intercept，uri：[{}]，pass", requestURI);
        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * 校验token并提取userId
     *
     * @param request
     * @return
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        // 从请求头或请求参数中获取 accessToken
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        // 使用 JwtUtil 解析 accessToken 获取 userId
        Object userId = JWTUtil.analyzeToken(accessToken, BaseConstant.LOGIN_USER_ID);
        if (EmptyUtil.isEmpty(userId)) {
            return false;
        }
        // 利用 userid 作为 key，从缓存中获取对应的value，即 accessToken，并进行比对
        String cacheToken = stringRedisTemplate.opsForValue().get(BaseConstant.USER_LOGIN_PREFIX + userId);

        if (StringUtils.isBlank(cacheToken)) {
            return false;
        }
        // 如果比对成功，将 userId 存入线程上下文
        if (!Objects.equals(accessToken, cacheToken)) {
            return false;
        }
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
        return true;
    }

    /**
     * 检查方法上是否存在 LoginIgnore 注解。如果存在，则不需要校验登录信息；否则，需要校验。
     *
     * @param proceedingJoinPoint
     * @return true 需要校验登录信息 false 不需要
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
