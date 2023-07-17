package com.example.studycirclebackend.controller.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.studycirclebackend.pojo.Ticket;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.TicketService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.RedisUtil;
import com.example.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

@Component
public class TicketInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TicketInterceptor.class);
    private static final String TOKEN_NAME = "token";
    @Resource
    private UserUtil userUtil;
    @Resource
    private TicketService ticketService;
    @Resource
    private UserService userService;
@Resource
private RedisTemplate<String, Object> redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取 cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 找到 cookie
                if (TOKEN_NAME.equals(cookie.getName())){
                    // 拿到 token
                    String token = cookie.getValue();
                    // 检验 token
                    if (token != null) {
                        // 得到 ticket
                        Ticket ticket = ticketService.getTicket(token);

                        // 存储 user（ticket存在，ticket有效，ticket没有过期）
                        if (ticket != null && ticket.getIsValid() == 1) {
                            User user = userService.getById(ticket.getUserId());
                            userUtil.setUser(user);
                            logger.info("login: " + user);
                        }
                    }
                }
            }
        }
        return true;
    }
}
