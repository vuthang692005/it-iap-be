package com.example.it_iap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ItIapApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItIapApplication.class, args);
    }

}
