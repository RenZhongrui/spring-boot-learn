package com.learn.springtask.configuration;


import com.learn.springtask.schedule.QuartzTask1;
import com.learn.springtask.schedule.QuartzTask2;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Value("${scedule.enable}")
    private boolean enable;

    // 使用SimpleScheduleBuilder来执行定时配置
    @Bean
    public Trigger simpleTrigger() {
        //5秒执行一次 SimpleScheduleBuilder
        if (!enable) return null;
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5) // 5秒一次
                .repeatForever(); //一直执行
        return TriggerBuilder.newTrigger().forJob(testQuartz1())
                .withIdentity("QuartzTask1")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public JobDetail testQuartz1() {
        return JobBuilder.newJob(QuartzTask1.class).withIdentity("QuartzTask1").storeDurably().build();
    }

    // 使用cron方式，每隔5秒执行一次
    @Bean
    public Trigger cronTrigger() {
        if (!enable) return null;
        return TriggerBuilder.newTrigger().forJob(testQuartz2())
                .withIdentity("QuartzTask2")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail testQuartz2() {
        return JobBuilder.newJob(QuartzTask2.class).withIdentity("QuartzTask2").storeDurably().build();
    }
}
