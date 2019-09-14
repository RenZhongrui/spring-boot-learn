package com.learn.interceptor.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 面向切面：如同三峡大坝拦截长江，所有的操作都要经过这个切面
 * 特点：
 * 切点：在什么地方和什么时候起作用
 * 增强方法：业务处理
 * 优点：使用aspect可以获取controller的请求方法和参数
 */
@Aspect
@Component
public class TimeAspect {

    /**
     * 表示InterceptorController下的所有方法
     *
     * @param pjp 表示代表了拦截的对象的信息
     * @return
     */
    @Around("execution(* com.learn.interceptor.controller.InterceptorController.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("InterceptorController");
        // 获取url传入的参数
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            System.out.println("arg::" + arg);
        }
        // 获取request对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        System.out.println("aspect request url:" + request.getRequestURL());
        String method = request.getMethod();
        System.out.println("method::" + method);

        // 获取注解类型,在controller上的注解@RequestMapping("/interceptor")
        Class<?> targetClass = pjp.getTarget().getClass();
        RequestMapping declaredAnnotation = targetClass.getDeclaredAnnotation(RequestMapping.class);
        String[] type = declaredAnnotation.value();
        for (int i = 0; i < type.length; i++) {
            System.out.println(" controller type:" + type[i]); // /interceptor
        }

        // 获取每个controller的method类型的集合GET、POST、PUT、DELETE
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method declaredMethod = targetClass.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        RequestMapping mapping = declaredMethod.getAnnotation(RequestMapping.class);
        RequestMethod[] methods = mapping.method();
        for (RequestMethod m : methods) {
            System.out.println("当前请求的method类型：" + m); // 如果使用GetMapping之类，则会报空指针
        }

        // 表示执行被拦截的方法
        Object proceed = pjp.proceed();
        return proceed;
    }

}
