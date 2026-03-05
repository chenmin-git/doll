package com.doll;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.doll.mapper")
public class DollApplication {
    public static void main(String[] args) {
        SpringApplication.run(DollApplication.class, args);
    }
}
