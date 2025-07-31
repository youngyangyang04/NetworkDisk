package com.disk.recycle;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication(scanBasePackages = {"com.disk.recycle"})
public class NetworkdiskRecycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskRecycleApplication.class, args);
    }

}
