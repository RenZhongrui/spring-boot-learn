package com.learn.async.controller;

import com.learn.async.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class AsyncController {

    @Autowired
    AsyncService asyncService;

    @GetMapping
    public String hello() {
        asyncService.hello();
        return "hello world";
    }
}
