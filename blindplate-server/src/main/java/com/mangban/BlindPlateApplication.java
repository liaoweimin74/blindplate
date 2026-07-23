package com.mangban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlindPlateApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlindPlateApplication.class, args);
    }
}
