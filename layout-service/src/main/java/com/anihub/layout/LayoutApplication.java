package com.anihub.layout;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.anihub.layout.mapper")
public class LayoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(LayoutApplication.class, args);
    }
}
