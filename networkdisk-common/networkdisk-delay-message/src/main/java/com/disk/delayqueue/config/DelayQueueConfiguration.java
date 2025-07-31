package com.disk.delayqueue.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Configuration
public class DelayQueueConfiguration {

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    @Bean("delayQueueThreadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程核心数目
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        //配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());

        threadPoolTaskExecutor.setThreadNamePrefix("delay-queue-thread-");
        //配置拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.info("线程池内加入任务被拒绝,使用当前线程执行: {}", r);
            //抛异常
            new ThreadPoolExecutor.CallerRunsPolicy();
        });
        //数据初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
