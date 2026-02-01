package com.disk.mq.config;

import com.disk.mq.producer.StreamProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weikunkun
 */
@Configuration
public class StreamConfiguration {
    @Bean
    public StreamProducer streamProducer() {
        StreamProducer streamProducer = new StreamProducer();
        return streamProducer;
    }
}
