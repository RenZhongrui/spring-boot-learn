package com.learn.springtask.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SpringTaskTest {

    private static final long SECOND = 1000;

    // 每过3秒执行一次
    @Scheduled(fixedRate = 3 * SECOND)
    public void task1() {
        System.out.println("每过3秒执行一次");
    }

    // 固定延迟3秒，从前一次任务结束开始计算，延迟3秒执行
    @Scheduled(fixedDelay = 3000)
    public void task2(){
        System.out.println("固定延迟3秒，从前一次任务结束开始计算，延迟3秒执行");
    }

    // 每5秒执行一次
    @Scheduled(cron = "0/5 * * ? * ?")
    public void test() {
        System.out.println("每5秒执行一次定时任务");
    }
}
