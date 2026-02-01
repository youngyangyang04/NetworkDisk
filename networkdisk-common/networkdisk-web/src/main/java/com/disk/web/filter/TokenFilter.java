package com.disk.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class TokenFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    public static final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    private RedissonClient redissonClient;

    public TokenFilter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器初始化，可选实现
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // 从请求头中获取Token
            String token = httpRequest.getHeader("Authorization");

            if (token == null || "null".equals(token) || "undefined".equals(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("No Token Found ...");
                logger.error("no token found in header , pls check!");
                return;
            }

            // 校验Token的有效性
            boolean isValid = checkTokenValidity(token);

            if (!isValid) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid or expired token");
                logger.error("token validate failed , pls check!");
                return;
            }

            // Token有效，继续执行其他过滤器链
            chain.doFilter(request, response);
        } finally {
            tokenThreadLocal.remove();
        }
    }

    private boolean checkTokenValidity(String token) {
        String luaScript = """
                local value = redis.call('GET', KEYS[1])
                redis.call('DEL', KEYS[1])
                return value""";

        // 6.2.3以上可以直接使用GETDEL命令
        // String value = (String) redisTemplate.opsForValue().getAndDelete(token);

        String result = (String) redissonClient.getScript().eval(RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.STATUS,
                Arrays.asList(token));

        tokenThreadLocal.set(result);
        return result != null;
    }

    @Override
    public void destroy() {
    }
}
