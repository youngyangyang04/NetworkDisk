package com.disk.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableDubbo
@SpringBootApplication
public class NetworkdiskUserProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskUserProviderApplication.class, args);
    }

}
