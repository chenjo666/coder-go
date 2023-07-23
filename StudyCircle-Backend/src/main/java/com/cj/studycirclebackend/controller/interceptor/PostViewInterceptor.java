package com.cj.studycirclebackend.controller.interceptor;

import com.cj.studycirclebackend.util.RedisUtil;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.regex.*;

@Component
public class PostViewInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PostViewInterceptor.class);

    @Resource
    private UserUtil userUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (userUtil.getUser() == null) {
            return true;
        }
        String path = request.getServletPath();
        String method = request.getMethod();
        if (isCorrectPath(path) && method.equals("GET")) {
            logger.info("用户[{}]使用[{}]访问了[{}]", userUtil.getUser().getId(), method, path);
            // 获取帖子 id
            Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String postId = (String) pathVariables.get("postId");
            // 使用 HyperLogLog 存储访问量
            String key = RedisUtil.getPostViewKey(Long.parseLong(postId));
            redisTemplate.opsForHyperLogLog().add(key, userUtil.getUser().getId());
        }
        return true;
    }

    // 判断是否为需要统计访问量的路径
    private boolean isCorrectPath(String path) {
        String pattern = "^/posts/\\d+$";
        return Pattern.compile(pattern).matcher(path).matches();
    }
}

