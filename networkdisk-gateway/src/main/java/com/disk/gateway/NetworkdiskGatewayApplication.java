package com.disk.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.disk.gateway")
public class NetworkdiskGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkdiskGatewayApplication.class, args);
    }

}
