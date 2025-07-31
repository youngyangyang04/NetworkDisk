package com.disk.share;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication(scanBasePackages = "com.disk")
public class NetworkdiskShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskShareApplication.class, args);
    }

}
