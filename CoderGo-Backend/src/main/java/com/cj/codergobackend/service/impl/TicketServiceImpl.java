package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.dao.TicketMapper;
import com.cj.codergobackend.pojo.Ticket;
import com.cj.codergobackend.service.TicketService;
import com.cj.codergobackend.util.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    @Resource
    private RedisTemplate<String, Object>  redisTemplate;
    @Override
    public Cookie createTicket(String token, Long userId) {
        // 生成登录凭证
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(3600 * 24 * 30); // 30天
        cookie.setPath("/"); // 设置 cookie 的作用路径，根路径下的所有页面都可以访问该 cookie

        // 生成一个登录凭证关联 token
        Ticket ticket = new Ticket();
        ticket.setToken(token);
        ticket.setUserId(userId);
        ticket.setIsValid(1);

        String key = RedisUtil.getTicketKey(token);
        redisTemplate.opsForValue().set(key, ticket, 30, TimeUnit.DAYS);

        return cookie;
    }

    @Override
    public Ticket getTicket(String token) {
        String key = RedisUtil.getTicketKey(token);
        return (Ticket) redisTemplate.opsForValue().get(key);
    }
}
