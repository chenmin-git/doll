package com.doll;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.doll.mapper")
public class DollApplication {
    public static void main(String[] args) {
        SpringApplication.run(DollApplication.class, args);
    }
}
