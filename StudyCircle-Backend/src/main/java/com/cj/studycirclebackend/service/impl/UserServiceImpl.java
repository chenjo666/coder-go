package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.enums.UserStatus;
import com.cj.studycirclebackend.enums.UserType;
import com.cj.studycirclebackend.event.Event;
import com.cj.studycirclebackend.event.SendMailEvent;
import com.cj.studycirclebackend.event.Topic;
import com.cj.studycirclebackend.pojo.Ticket;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.FollowService;
import com.cj.studycirclebackend.service.TicketService;
import com.cj.studycirclebackend.service.UserService;
import com.cj.studycirclebackend.util.DataUtil;
import com.cj.studycirclebackend.util.RedisUtil;
import com.cj.studycirclebackend.vo.UserVO;
import com.cj.studycirclebackend.dao.UserMapper;
import com.cj.studycirclebackend.event.EventProducer;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private TicketService ticketService;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private FollowService followService;
    @Resource
    private UserUtil userUtil;
    @Override
    public Response login(String email, String password, HttpServletResponse response) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            return Response.badRequest();
        }
        // 1. 查询用户
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        // 1.1 用户不存在，则退出
        if (user == null) {
            return Response.loginFailed();
        }
        // 1.2 用户存在则解密密码
        password = DataUtil.md5(password + user.getSalt());
        // 2.1 登录失败
        if (!password.equals(user.getPassword())) {
            return Response.loginFailed();
        }
        // 2.2 登录成功
        String token = DataUtil.generateUUID();
        Cookie cookie = ticketService.createTicket(token, user.getId());
        response.addCookie(cookie);

        logger.info("User /login: {}", user);
        return Response.loginSuccess(user);
    }

    @Override
    public Response register(String email, String password, String code, HttpServletResponse response) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)  || StringUtils.isBlank(password)) {
            return Response.badRequest();
        }
        // 1. 查询用户
        User user = getOne(new QueryWrapper<User>().eq("email", email).eq("activation_code", code));
        // 1.1 用户不存在
        if (user == null) {
            return Response.registerFailed();
        }
        // 1.2 注册成功，修改用户状态
        String token = DataUtil.generateUUID();
        user.setIsRegister(UserStatus.activated.getValue());
        user.setSalt(token.substring(0, 8));
        user.setPassword(DataUtil.md5(password + user.getSalt()));
        user.setRegisterTime(new Date());
        updateById(user);

        // 2. 保存 token
        Cookie cookie = ticketService.createTicket(token, user.getId());
        response.addCookie(cookie);

        logger.info("User /register: {}", email);
        return Response.registerSuccess(user);
    }

    @Override
    public Response activate(String email) {
        if (email == null) {
            return Response.badRequest();
        }
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        // 用户已经注册并且已经激活
        if (user != null && user.getIsRegister() == 1) {
            return Response.activateFailed();
        }
        // 用户注册但未激活
        String uuidCode = DataUtil.generateUUID();

        // 异步发送邮件
        Event mailEvent = new SendMailEvent(Topic.MAIL, NoticeType.SEND_MAIL.getValue(), email, "您正在注册学友圈账号，这是您的验证码", uuidCode);
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
        if (StringUtils.isBlank(token)) {
            return Response.badRequest();
        }
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





    @Override
    public Response followUser(Long targetUserId) {
        if (targetUserId == null || userUtil.getUser() == null) {
            return Response.badRequest();
        }
        followService.createFollowUser(userUtil.getUser().getId(), targetUserId);
        return Response.ok();
    }

    @Override
    public Response unFollowUser(Long targetUserId) {
        if (targetUserId == null || userUtil.getUser() == null) {
            return Response.badRequest();
        }
        followService.deleteFollowUser(userUtil.getUser().getId(), targetUserId);

        return Response.ok();
    }

    @Override
    public Response getUserFollowings(Long userId) {
        if (userId == null) {
            return Response.badRequest();
        }
        Set<Object> followings = followService.getUserFollowings(userId);
        return getUserVOListBySet(followings);
    }
    @Override
    public Response getUserFollowers(Long userId) {
        if (userId == null) {
            return Response.badRequest();
        }
        Set<Object> followers = followService.getUserFollowers(userId);
        return getUserVOListBySet(followers);
    }
    private Response getUserVOListBySet(Set<Object> followings) {
        List<Long> followingsId = followings.stream()
                .map(obj -> (Long) obj)
                .toList();

        List<User> users = getBaseMapper().selectBatchIds(followingsId);
        List<UserVO> userVOList = getUserVOList(users);

        return Response.ok(userVOList);
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
