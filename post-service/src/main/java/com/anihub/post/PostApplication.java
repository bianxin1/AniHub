package com.anihub.post;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@MapperScan("com.anihub.post.mapper")
public class PostApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostApplication.class, args);
    }
}