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

@Aspect
@Component
@Order(2)
public class ArgsValidationAspect {
    private static final Logger logger = LoggerFactory.getLogger(ArgsValidationAspect.class);
    @Around("execution(* com.cj.studycirclebackend.controller.*.*(..))  && !execution(* com.cj.studycirclebackend.controller.PostController.searchPosts(..))")
    public Object validateParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        // 验证参数
        for (Object arg : args) {
            logger.info("{}", arg);
            if (arg == null || (arg instanceof String && StringUtils.isBlank((CharSequence) arg))) {
                return Response.badRequest();
            }
        }
        // 继续执行原始方法
        return joinPoint.proceed();
    }
}
