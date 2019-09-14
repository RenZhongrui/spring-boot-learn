package com.learn.interceptor.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

@Component
public class TimeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("time filter init");
    }

    /**
     * 优点：可以获取controller的请求类
     * 缺点：使用filter无法知道是哪个controller的方法处理的
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("time filter doFilter start");
        long start = new Date().getTime();
        chain.doFilter(request, response); // 执行下一个请求
        long end = new Date().getTime();
        System.out.println("耗时：" + (end - start));
        System.out.println("time filter doFilter finish");
    }

    @Override
    public void destroy() {
        System.out.println("time filter destroy");
    }
}
