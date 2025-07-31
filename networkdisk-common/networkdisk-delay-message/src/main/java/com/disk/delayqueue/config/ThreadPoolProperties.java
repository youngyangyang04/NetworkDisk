package com.disk.delayqueue.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@Configuration
@Getter
@Setter
public class ThreadPoolProperties {

   @Value("${delayqueue.pool.core-pool-size : 2}")
   private Integer corePoolSize;

   @Value("${delayqueue.pool.max-pool-size : 4}")
   private Integer maxPoolSize;

   @Value("${delayqueue.pool.queue-capacity : 200}")
   private Integer queueCapacity;

   @Value("${delayqueue.pool.keep-alive-seconds : 60}")
   private Integer keepAliveSeconds;


}
