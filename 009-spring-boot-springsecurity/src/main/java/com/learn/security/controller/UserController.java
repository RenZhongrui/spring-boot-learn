package com.learn.security.controller;

import com.learn.security.common.MessageConst;
import com.learn.security.entity.Result;
import com.learn.security.service.UserService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    //依赖注入
    @Reference
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/login")
    public Result login(String username, String password){
        System.out.println("oms backend====u:"+username+" ,p:"+password);
        if (userService.login(username, password)){
            System.out.println("login ok!!!");
            return new Result(true, MessageConst.ACTION_SUCCESS);
        } else {
            System.out.println("login fail");
            return new Result(false, MessageConst.ACTION_FAIL);
        }
    }
}
