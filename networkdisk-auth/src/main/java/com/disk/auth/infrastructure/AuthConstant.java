package com.disk.auth.infrastructure;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class AuthConstant {

    public static final String TOKEN_PREFIX = "token:";

    /**
     * 默认登录超时时间：7天
     */
    public static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 验证码字符数量
     */
    public static final int CAPTCHA_LENGTH = 5;

    /**
     * 过期时间
     */
    public static final long CAPTCHA_EXPIRES = 0;

    /**
     * 登录验证码
     */
    public static final String CAPTCHA_VERIFY_LOGIN = "verifyCodeLogin";

    /**
     * 邮箱验证码
     */
    public static final String CAPTCHA_VERIFY_EMAIL = "verifyCodeEmail";

    /**
     * 登录用户的用户ID的key值
     */
    public static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    /**
     * 用户登录缓存前缀
     */
    public static final String USER_LOGIN_PREFIX = "USER_LOGIN_";

    /**
     * 一天的毫秒值
     */
    public static final Long ONE_DAY_TIME_MILLS = 24L * 60L * 60L * 1000L;
}
