package com.disk.web.config;

import com.disk.web.filter.TokenFilter;
import com.disk.web.handler.GlobalWebExceptionHandler;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    GlobalWebExceptionHandler globalWebExceptionHandler() {
        return new GlobalWebExceptionHandler();
    }

    /**
     * 注册token过滤器
     *
     * @param redissonClient
     * @return
     */
    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilter(RedissonClient redissonClient) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TokenFilter(redissonClient));
        registrationBean.addUrlPatterns("/trade/buy");
        registrationBean.setOrder(10);

        return registrationBean;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*")  // 允许的跨域来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的方法
                .allowedHeaders("*")  // 允许的请求头
                .allowCredentials(true);
//                .maxAge(3600);  // 预检请求的有效期
    }
}
