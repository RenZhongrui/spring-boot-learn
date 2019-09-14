package com.learn.log.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;

// 拦截请求，使用slf4j的MDC功能将请求相关的信息拼入日志格式中,在xml配置文件中
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String REMOTER_ID= "requestId";
    private static final String REMOTER_HOST = "host";
    private static final String REMOTER_ADDR = "remoter_addr";
    private static final String REMOTER_QUERY = "remoter_query";
    private static final String REMOTER_METHOD = "remoter_method";
    private static final String REMOTER_URL= "remoter_url";
    private static final String REMOTER_AGENT= "remoter_url";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("请求开始");
        log.info("host:" + request.getRemoteHost());
        log.info("addr:" + request.getRemoteAddr());
        log.info("query:" + request.getQueryString());
        log.info("method:" + request.getMethod());
        log.info("url:" + request.getRequestURL());
        log.info("agent:" + request.getHeader("User-Agent"));
        MDC.put(REMOTER_ID, UUID.randomUUID().toString());
        MDC.put(REMOTER_HOST, request.getRemoteHost());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("请求处理");
        MDC.remove(REMOTER_HOST);
        MDC.remove(REMOTER_ID);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("请求完成");
    }
}
