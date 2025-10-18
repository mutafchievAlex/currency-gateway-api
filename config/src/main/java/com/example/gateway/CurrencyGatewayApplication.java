package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyGatewayApplication.class, args);
    }
}
