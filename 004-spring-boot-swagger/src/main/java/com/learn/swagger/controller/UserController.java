package com.learn.swagger.controller;

import com.learn.swagger.entity.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

/**
 * 1、使用URL描述资源
 * 2、使用HTTP方法描述行为，使用HTTP状态码表示不同的结果
 * 3、使用json交互数据，包括传参和返参
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    // 第三步，使用注解@ApiOperation(value = "根据用户名查询用户信息")，对接口进行描述
    @ApiOperation(value = "根据用户名查询用户信息")
    public List<User> query(@RequestParam String userName) {
        List<User> users = new ArrayList<>();
        users.add(new User(userName, "123456"));
        return users;
    }

    // 多个参数
    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    @ApiOperation(value = "根据用户名和年龄查询用户信息")
    public List<User> queries(@RequestParam String userName, @RequestParam Integer age) {
        if ("ren".equals(userName)) {
            throw new RuntimeException("用户名不能为ren");
        }
        List<User> users = new ArrayList<>();
        users.add(new User(userName, "123456"));
        return users;
    }

/*

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<User> queryTest(@RequestParam(name = "name", required = false, defaultValue = "admin") String userName) {
        // 传过来的参数是name，userName就表示name
        return null;
    }*/
}
