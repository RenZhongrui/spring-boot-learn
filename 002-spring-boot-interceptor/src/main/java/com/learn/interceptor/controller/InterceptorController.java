package com.learn.interceptor.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interceptor")
public class InterceptorController {

    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public String test(@PathVariable Integer id) {
        return "Hello Interceptor";
    }

    @RequestMapping( value = "", method = RequestMethod.POST)
    public String getData(@PathVariable Integer id) {
        return "Hello Interceptor";
    }
}
