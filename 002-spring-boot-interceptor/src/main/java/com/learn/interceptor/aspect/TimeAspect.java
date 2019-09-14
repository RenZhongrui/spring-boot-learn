package com.learn.interceptor.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 面向切面：如同三峡大坝拦截长江，所有的操作都要经过这个切面
 * 特点：
 *  切点：在什么地方和什么时候起作用
 *  增强方法：业务处理
 *  优点：使用aspect可以获取controller的请求方法和参数
 */
@Aspect
@Component
public class TimeAspect {

    /**
     * 表示InterceptorController下的所有方法
     * @param pjp 表示代表了拦截的对象的信息
     * @return
     */
    @Around("execution(* com.learn.interceptor.controller.InterceptorController.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("InterceptorController");
        // 获取url传入的参数
        Object[] args = pjp.getArgs();
        for (Object arg: args) {
            System.out.println("arg::" + arg);
        }
        // 表示执行被拦截的方法
        Object proceed = pjp.proceed();
        return proceed;
    }

}
