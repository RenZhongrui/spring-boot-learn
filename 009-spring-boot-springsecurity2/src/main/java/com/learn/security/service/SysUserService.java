package com.learn.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learn.security.dao.entity.SysUser;
import com.learn.security.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {

    @Autowired
    SysUserMapper sysUserMapper;

    // 通过用户名获取用户信息
    public SysUser getByName(String username) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        return user;
    }
}
