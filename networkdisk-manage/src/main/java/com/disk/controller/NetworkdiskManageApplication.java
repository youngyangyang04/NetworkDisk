package com.disk.controller;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class NetworkdiskManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskManageApplication.class, args);
    }

}
