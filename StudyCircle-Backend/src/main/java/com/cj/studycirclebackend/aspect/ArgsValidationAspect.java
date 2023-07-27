package com.cj.studycirclebackend.aspect;

import com.cj.studycirclebackend.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Aspect
@Component
@Order(2)
public class ArgsValidationAspect {
    private static final Logger logger = LoggerFactory.getLogger(ArgsValidationAspect.class);
    @Around("execution(* com.cj.studycirclebackend.controller.*.*(..))  " +
            "&& !execution(* com.cj.studycirclebackend.controller.DiscussionController.searchPostsByMysql(..))" +
            "&& !execution(* com.cj.studycirclebackend.controller.DiscussionController.searchPostsByElasticsearch(..))")
    public Object validateParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        // 验证参数
        for (Object arg : args) {
            // Integer: 要求不能为负数
            if (arg instanceof Integer) {
                logger.info("Integer args: {}", arg);
                if ((Integer) arg < 0){
                    return Response.badRequest();
                }
            }
            if (arg instanceof Long) {
                logger.info("Long args: {}", arg);
                if ((Long) arg < 0) {
                    return Response.badRequest();
                }
            }
            if (arg instanceof String) {
                logger.info("String args: {}", arg);
                if (StringUtils.isBlank((CharSequence) arg)){
                    return Response.badRequest();
                }
            }
            if (arg instanceof Map) {
                logger.info("Map args: {}", arg);
            }
            if (arg instanceof List) {
                logger.info("list args: {}", arg);
            }
            if (arg == null || (arg instanceof String && StringUtils.isBlank((CharSequence) arg))) {
                return Response.badRequest();
            }
        }
        // 继续执行原始方法
        return joinPoint.proceed();
    }
}
