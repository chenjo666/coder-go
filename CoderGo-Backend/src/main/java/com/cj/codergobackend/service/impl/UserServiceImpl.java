package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.enums.UserStatus;
import com.cj.codergobackend.enums.UserType;
import com.cj.codergobackend.event.Event;
import com.cj.codergobackend.event.MailSendEvent;
import com.cj.codergobackend.pojo.Ticket;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.TicketService;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import com.cj.codergobackend.util.RedisUtil;
import com.cj.codergobackend.vo.UserVO;
import com.cj.codergobackend.dao.UserMapper;
import com.cj.codergobackend.event.EventProducer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private TicketService ticketService;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Response login(String email, String password, HttpServletResponse response) {
        // 1. 查询用户
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        // 1.1 用户不存在，则退出
        if (user == null) {
            return Response.notContent();
        }
        // 1.2 用户存在则解密密码
        password = DataUtil.md5(password + user.getSalt());
        // 2.1 登录失败
        if (!Objects.equals(password, user.getPassword())) {
            return Response.notContent();
        }
        // 2.2 登录成功
        String token = DataUtil.generateUUID();
        Cookie cookie = ticketService.createTicket(token, user.getId());
        response.addCookie(cookie);

        logger.info("User /login: {}", user);
        return Response.ok(user);
    }

    @Override
    public Response register(String email, String password, String code, HttpServletResponse response) {
        // 1. 查询用户
        User user = getOne(new QueryWrapper<User>().eq("email", email).eq("activation_code", code));
        // 1.1 用户不存在
        if (user == null) {
            return Response.notContent();
        }
        // 1.2 注册成功，修改用户状态
        String token = DataUtil.generateUUID();
        user.setIsRegister(UserStatus.activated.getValue());
        user.setSalt(token.substring(0, 8));
        user.setPassword(DataUtil.md5(password + user.getSalt()));
        user.setCreatedAt(new Date());
        updateById(user);

        // 2. 保存 token
        Cookie cookie = ticketService.createTicket(token, user.getId());
        response.addCookie(cookie);

        logger.info("User /register: {}", email);
        return Response.ok(user);
    }

    @Override
    public Response activate(String email) {
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        // 用户已经注册并且已经激活
        if (user != null && user.getIsRegister() == 1) {
            return Response.activateFailed();
        }
        // 用户注册但未激活
        String uuidCode = DataUtil.generateUUID();

        // 同步发送邮件
//        emailUtil.send(email, "您正在注册学友圈账号，这是您的验证码", uuidCode);

        // 异步发送邮件
        Event mailEvent = new MailSendEvent(NoticeTopic.MAIL_SEND, NoticeType.MAIL_SEND, email, "您正在注册学友圈账号，这是您的验证码", uuidCode);
        eventProducer.createEvent(mailEvent);

        if (user != null) {
            // 增加激活码
            user.setActivationCode(uuidCode);
            updateById(user);
            return Response.activateSuccess();
        }
        // 用户未注册
        User u = new User();
        u.setEmail(email);
        u.setUsername(DataUtil.generateRandomUsername());
        u.setType(UserType.NORMAL.getValue());
        u.setIsRegister(UserStatus.unActivated.getValue());
        u.setActivationCode(uuidCode);
        save(u);

        return Response.activateSuccess();
    }

    @Override
    public Response logout(String token) {
        // 1. 得到凭证
        Ticket ticket = ticketService.getTicket(token);
        if (ticket == null) {
            return Response.notContent();
        }
        // 2. 删除凭证
        String key = RedisUtil.getTicketKey(token);
        redisTemplate.delete(key);

        logger.info("User logout: {}", ticket.getUserId());
        return Response.ok();
    }
    public List<UserVO> getUsersBySet(Set<Object> userIds) {
        List<Long> uIds = userIds.stream()
                .map(obj -> (Long) obj)
                .toList();
        List<User> users = listByIds(uIds);
        return getUserVOList(users);
    }
    @Override
    public UserVO getUserVO(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getAvatar());
    }


    private List<UserVO> getUserVOList(List<User> users) {
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : users) {
            userVOList.add(getUserVO(user));
        }
        return userVOList;
    }
}
