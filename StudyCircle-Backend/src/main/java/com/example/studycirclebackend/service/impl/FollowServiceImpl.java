package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.FollowMapper;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.ResponseCode;
import com.example.studycirclebackend.enums.ResponseMsg;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.pojo.Follow;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.FollowService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private UserUtil userUtil;
    @Resource
    private UserService userService;
    @Resource
    private EventProducer eventProducer;
    @Override
    public Response createFollow(Long userId) {
        if (userId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        Follow follow = new Follow();
        follow.setUserFromId(userUtil.getUser().getId());
        follow.setUserToId(userId);

        // 关注事件
        Event event = Event.builder()
                .topic("follow")
                .userFromId(userUtil.getUser().getId())
                .userToId(userId)
                .build();
        eventProducer.createEvent(event);

        boolean result = save(follow);
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response deleteFollow(Long userId) {
        if (userId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        boolean result = remove(new QueryWrapper<Follow>()
                .eq("user_from_id", userUtil.getUser().getId())
                .eq("user_to_id", userId));
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public boolean isFollowed(Long userFromId, Long userToId) {
        if (userFromId == null || userToId == null) {
            return false;
        }
        return getOne(new QueryWrapper<Follow>()
                .eq("user_from_id", userFromId)
                .eq("user_to_id", userToId)) != null;
    }

    @Override
    public Response getStars(Long userId) {
        if (userId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        // 1. 得到关注列表
        List<User> stars = userService.list(new QueryWrapper<User>()
                .inSql("id", "SELECT user_to_id FROM follow WHERE user_from_id = " + userId));
        // 2. 转成 VO 对象
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : stars) {
            UserVO userVO = userService.convertToVO(user);
            userVOList.add(userVO);
        }

        return Response.builder()
                .code(ResponseCode.SUCCESS.getValue())
                .data(userVOList)
                .build();
    }

    @Override
    public Response getFans(Long userId) {
        if (userId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        // 1. 得到关注列表
        List<User> stars = userService.list(new QueryWrapper<User>()
                .inSql("id", "SELECT user_from_id FROM follow WHERE user_to_id = " + userId));
        // 2. 转成 VO 对象
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : stars) {
            UserVO userVO = userService.convertToVO(user);
            userVOList.add(userVO);
        }

        return Response.builder()
                .code(ResponseCode.SUCCESS.getValue())
                .data(userVOList)
                .build();
    }
}
