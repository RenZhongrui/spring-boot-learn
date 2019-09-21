package com.learn.springtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 1、添加@EnableScheduling注解，开启Spring的定时任务功能
@EnableScheduling
@SpringBootApplication
public class SpringTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTaskApplication.class, args);
    }

}
