package com.learn.async.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    @Async //第二步，使用注解@Async表示此方法是异步方法
    public void hello(){
        try {
            Thread.sleep(3000);
            System.out.println("异步睡了3秒中");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
