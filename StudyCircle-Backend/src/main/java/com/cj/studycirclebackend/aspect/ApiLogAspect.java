package com.cj.studycirclebackend.aspect;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@Order(1)
public class ApiLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ApiLogAspect.class);
    @Pointcut("execution(* com.cj.studycirclebackend.controller.*.*(..))")
    public void apiLog(){}

    @Before("apiLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("===========================开始===========================");
        logger.info("请求路径:  {}", request.getRequestURL().toString());
        logger.info("请求方法:  {}", request.getMethod()); // 打印 Http method
        logger.info("请求接口:  {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()); // 打印调用 controller 的全路径以及执行方法
        logger.info("请求参数:  {}", JSONObject.toJSONString(joinPoint.getArgs())); // 打印请求入参
        logger.info("用户地址:  {}", request.getRemoteAddr());                      // 打印请求的 IP
    }

    @Around("apiLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        logger.info("响应结果:  {}", JSONObject.toJSONString(result));
        logger.info("消耗时间:  {}ms", System.currentTimeMillis() - startTime);
        logger.info("===========================结束===========================");
        return result;
    }
}
