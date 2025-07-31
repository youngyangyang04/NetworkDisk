package com.disk.base.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
public class JWTUtil {

    public static final Long TWO_LONG = 2L;

    /**
     * 秘钥
     */
    private final static String JWT_PRIVATE_KEY = "1AC16040B51140E49F2F93A7BE222C57";

    /**
     * 刷新时间
     */
    private final static String RENEWAL_TIME = "RENEWAL_TIME";

    /**
     * 生成token
     * 生成一个 JWT，包含主体信息、声明（claims）和过期时间。
     * @param subject
     * @param claimKey
     * @param claimValue
     * @param expire
     * @return
     */
    public static String generateToken(String subject, String claimKey, Object claimValue, Long expire) {
       return Jwts.builder()
                .subject(subject)
                .claim(claimKey, claimValue)
                .claim(RENEWAL_TIME, new Date(System.currentTimeMillis() + expire / TWO_LONG))
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(Keys.hmacShaKeyFor(JWT_PRIVATE_KEY.getBytes()))
                .compact();
    }

    /**
     * 解析token
     * 解析 JWT 并返回指定的声明值
     * @param token
     * @return
     */
    public static Object analyzeToken(String token, String claimKey) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(JWT_PRIVATE_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(claimKey);
        } catch (Exception e) {
            // 如果 token 无效或过期，解析过程会抛出异常
            // 捕获异常并返回 null
            return null;
        }
    }

    public static void main(String[] args) {
//        userInfo.getNickName(), AuthConstant.LOGIN_USER_ID, userInfo.getUserId(), AuthConstant.ONE_DAY_TIME_MILLS
        String s = generateToken("zhangsan", "LOGIN_USER_ID", 1, 24L * 60L * 60L * 1000L);
        System.out.println(s);
        Object loginUserId = analyzeToken(s, "LOGIN_USER_ID");
        System.out.println(loginUserId);
    }
}
