package com.learn.interceptor.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 使用intercept拦截请求
@Component
public class RequestInterceptor implements HandlerInterceptor {

    /**
     * 使用Interceptor可以获取controller的请求类和请求方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("RequestInterceptor请求URL：：" + request.getRequestURL());
        String className = ((HandlerMethod) handler).getBean().getClass().getName();
        System.out.println("class name:" + className);
        String methodName = ((HandlerMethod) handler).getMethod().getName();
        System.out.println("method name:" + methodName);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("RequestInterceptor postHandle：：");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("RequestInterceptor afterCompletion：：");
    }
}
