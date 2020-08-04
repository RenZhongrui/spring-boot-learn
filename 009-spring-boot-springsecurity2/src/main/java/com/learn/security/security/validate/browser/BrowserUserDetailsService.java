package com.learn.security.security.validate.browser;

import com.learn.security.dao.entity.SysUser;
import com.learn.security.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 浏览器表单登录 UserDetailsService
 * 在执行登录的过程中，这个方法将根据用户名去查找用户，如果用户不存在，
 * 则抛出UsernameNotFoundException异常，否则直接将查到的用户信息返回
 */
@Slf4j
@Service
public class BrowserUserDetailsService implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    // 通过用户名来获取用户
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        SysUser user = sysUserService.getByName(username);
        log.error(user.toString());
        // 判断用户是否存在
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
