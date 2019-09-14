package com.learn.log;

import com.learn.log.schedule.ScheduleTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // 日志要写在启动之后，不然日志配置不生效
        log.error("启动程序");
        // 测试Logger
        LoggerTest loggerTest = new LoggerTest();
        loggerTest.test();
        // 测试lombok中使用日志
        LombokTest lombokTest = new LombokTest();
        lombokTest.test();

        // 测试定时任务日志
        ScheduleTest scheduleTest = new ScheduleTest();
        scheduleTest.test();
    }

}
