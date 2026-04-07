package com.disk.ai;

import com.disk.ai.infrastructure.config.AiIndexProperties;
import com.disk.ai.infrastructure.config.AiProviderProperties;
import com.disk.ai.infrastructure.vector.PgVectorProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableDubbo
@SpringBootApplication(scanBasePackages = "com.disk")
@EnableConfigurationProperties({
        AiProviderProperties.class,
        AiIndexProperties.class,
        PgVectorProperties.class
})
public class NetworkdiskAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskAiApplication.class, args);
    }
}
