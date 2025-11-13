package com.abhedyam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbhedyamBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbhedyamBackendApplication.class, args);
    }

}
