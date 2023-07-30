package com.cj.studycirclebackend.controller.interceptor;

import com.cj.studycirclebackend.pojo.Ticket;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.TicketService;
import com.cj.studycirclebackend.service.UserService;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取 cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return true;
        }
        for (Cookie cookie : cookies) {
            // 找到 cookie
            if (TOKEN_NAME.equals(cookie.getName())){
                // 拿到 token
                String token = cookie.getValue();
                // 检验 token
                if (token != null) {
                    // 得到 ticket
                    Ticket ticket = ticketService.getTicket(token);
                    logger.info("token: {}, ticket: {}", token, ticket);
                    // 存储 user
                    if (ticket != null) {
                        User user = userService.getById(ticket.getUserId());
                        userUtil.setUser(user);
                        logger.info("login: " + user);
                    }
                }
            }
        }
        return true;
    }
    // 移除强引用value，排除内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userUtil.removeUser();
    }
}
