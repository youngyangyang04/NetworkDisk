package com.disk.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication(scanBasePackages = {"com.disk.auth"})
public class NetworkdiskAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskAuthApplication.class, args);
    }

}
