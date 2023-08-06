package com.cj.codergobackend.controller.interceptor;

import com.cj.codergobackend.util.RedisUtil;
import com.cj.codergobackend.util.UserUtil;
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
public class ArticleViewInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ArticleViewInterceptor.class);

    @Resource
    private UserUtil userUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (userUtil.getUser() == null) {
            logger.info("user not login!");
            return true;
        }
        String path = request.getServletPath();
        logger.info("path: {}", path);
        String method = request.getMethod();
        if (isCorrectPath(path) && method.equals("GET")) {
            logger.info("用户[{}]使用[{}]访问了[{}]", userUtil.getUser().getId(), method, path);
            // 获取文章 id
            Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String articleId = (String) pathVariables.get("articleId");
            // 使用 HyperLogLog 存储访问量
            String key = RedisUtil.getArticleViewKey(Long.parseLong(articleId));
            redisTemplate.opsForHyperLogLog().add(key, userUtil.getUser().getId());
        }
        return true;
    }

    // 判断是否为需要统计访问量的路径
    private boolean isCorrectPath(String path) {
        String pattern = "^/article/v1/articles/\\d+$";
        boolean res = Pattern.compile(pattern).matcher(path).matches();
        logger.info("res: {}", res);
        return res;
    }
}

