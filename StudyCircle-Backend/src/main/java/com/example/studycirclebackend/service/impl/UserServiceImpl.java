package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.TicketMapper;
import com.example.studycirclebackend.dao.UserMapper;
import com.example.studycirclebackend.dto.MailEvent;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.*;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.event.Topic;
import com.example.studycirclebackend.pojo.Ticket;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.TicketService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.DataUtil;
import com.example.studycirclebackend.util.EmailUtil;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.UserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private TicketService ticketService;
    @Resource
    private EmailUtil emailUtil;
    @Resource
    private EventProducer eventProducer;
    @Override
    public Response login(String email, String password, HttpServletResponse response) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        if (user == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.LOGIN_ERROR_DATA.getValue())
                    .build();
        }
        // 解密密码
        password = DataUtil.md5(password + user.getSalt());
        // 登录失败
        if (!password.equals(user.getPassword())) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.LOGIN_ERROR_DATA.getValue())
                    .build();
        }
        // 登录成功
        String token = DataUtil.generateUUID();
        Cookie cookie = saveToken(token, user.getId());
        response.addCookie(cookie);

        logger.info("user login: " + user);

        return Response.builder()
                .code(ResponseCode.SUCCESS.getValue())
                .msg(ResponseMsg.LOGIN_SUCCESS.getValue())
                .data(user)
                .build();
    }

    @Override
    public Response register(String email, String password, String code, HttpServletResponse response) {
        if (email == null || password == null || code == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        User user = getOne(new QueryWrapper<User>().eq("email", email).eq("activation_code", code));
        if (user == null) {
            return Response.builder().code(-1).msg("注册失败，请输入正确的信息！").build();
        }
        // 注册成功，修改用户状态
        String token = DataUtil.generateUUID();
        user.setIsRegister(UserStatus.activated.getValue());
        user.setSalt(token.substring(0, 8));
        user.setPassword(DataUtil.md5(password + user.getSalt()));
        user.setRegisterTime(new Date());
        updateById(user);

        // 保存 token
        Cookie cookie = saveToken(token, user.getId());
        response.addCookie(cookie);

        logger.info("/user/register: " + email);
        return Response.builder().code(200).data(user).msg("注册成功！").build();
    }

    @Override
    public Response activate(String email) {
        if (email == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        // 用户已经注册并且已经激活
        if (user != null && user.getIsRegister() == 1) {
            return Response.builder().code(-1).msg(ActivationResult.FAILURE.getResult()).build();
        }
        // 用户注册但未激活
        String uuidCode = DataUtil.generateUUID();


        // 普通发送邮件
//        emailUtil.send(email, "您正在注册学友圈账号，这是您的验证码", uuidCode);

        // 消息队列发送邮件
        MailEvent mailEvent = new MailEvent(Topic.MAIL, email, "您正在注册学友圈账号，这是您的验证码", uuidCode);
        eventProducer.sendMailEvent(mailEvent);

        if (user != null) {
            // 增加激活码
            user.setActivationCode(uuidCode);
            updateById(user);
            return Response.builder().code(200).msg(ActivationResult.SUCCESS.getResult()).build();
        }
        // 用户未注册
        User u = new User();
        u.setEmail(email);
        u.setUsername(DataUtil.generateRandomUsername());
        u.setType(UserType.NORMAL.getValue());
        u.setIsRegister(UserStatus.unActivated.getValue());
        u.setActivationCode(uuidCode);
        save(u);

        return Response.builder().code(200).msg(ActivationResult.SUCCESS.getResult()).build();
    }

    @Override
    public Response logout(String token) {
        if (token == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        Ticket ticket = ticketService.getOne(new QueryWrapper<Ticket>().eq("token", token));
        if (ticket == null) {
            return Response.builder().code(-1).msg("账户不存在！").build();
        }
        ticket.setIsValid(0);
        ticketService.updateById(ticket);
        logger.info("/user/logout: " + ticket.getUserId());
        return Response.builder().code(200).msg("退出登录成功，用户已退出登录！").build();
    }

    @Override
    public UserVO convertToVO(User user) {
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getAvatar());
        return null;
    }

    private Cookie saveToken(String token, Long userId) {
        // 生成登录凭证
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(3600 * 24 * 30); // 30天
        cookie.setPath("/"); // 设置 cookie 的作用路径，根路径下的所有页面都可以访问该 cookie

        // 生成一个登录凭证关联 token
        Ticket ticket = new Ticket();
        ticket.setToken(token);
        ticket.setUserId(userId);
        ticket.setIsValid(1);
        ticket.setExpire(new Date(System.currentTimeMillis() + 3600L * 24 * 30 * 1000));
        ticketService.save(ticket);

        return cookie;
    }
}
