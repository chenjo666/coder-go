package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.service.FollowService;
import com.cj.codergobackend.dao.FollowMapper;
import com.cj.codergobackend.event.Event;
import com.cj.codergobackend.event.EventProducer;
import com.cj.codergobackend.event.UserFollowEvent;
import com.cj.codergobackend.pojo.Follow;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.RedisUtil;
import com.cj.codergobackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Set;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private UserUtil userUtil;

    @Override
    public void createFollowUser(Long userFromId, Long userToId) {
        String followingKey = RedisUtil.getUserFollowingKey(userFromId);
        redisTemplate.opsForZSet().add(followingKey, userToId, System.currentTimeMillis());
        String followerKey = RedisUtil.getUserFollowerKey(userToId);
        redisTemplate.opsForZSet().add(followerKey, userFromId, System.currentTimeMillis());

        // 关注事件
        Event followEvent = new UserFollowEvent(NoticeTopic.USER_FOLLOW, NoticeType.USER_FOLLOW, userFromId, userToId);
        eventProducer.createEvent(followEvent);
    }

    @Override
    public void deleteFollowUser(Long userFromId, Long userToId) {
        String followingKey = RedisUtil.getUserFollowingKey(userFromId);
        redisTemplate.opsForZSet().remove(followingKey, userToId);
        String followerKey = RedisUtil.getUserFollowerKey(userToId);
        redisTemplate.opsForZSet().remove(followerKey, userFromId);
    }

    @Override
    public boolean isFollowedByUser(Long userFromId, Long userToId) {
        String key = RedisUtil.getUserFollowingKey(userFromId);
        return null != redisTemplate.opsForZSet().rank(key, userToId);
    }

    @Override
    public Set<Object> getUserFollowersSet(Long userId) {
        String key = RedisUtil.getUserFollowerKey(userId);
        return redisTemplate.opsForZSet().reverseRangeByScore(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Set<Object> getUserFollowingsSet(Long userId) {
        String key = RedisUtil.getUserFollowingKey(userId);
        return redisTemplate.opsForZSet().reverseRangeByScore(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public Response getUserFollowers(Long userId) {
        String key = RedisUtil.getUserFollowerKey(userId);
        Set<Object> userIds = redisTemplate.opsForZSet().reverseRangeByScore(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        assert userIds != null;
        if (userIds.size() == 0)
            return Response.notContent();
        return Response.ok(userService.getUsersBySet(userIds));
    }

    @Override
    public Response getUserFollowings(Long userId) {
        String key = RedisUtil.getUserFollowingKey(userId);
        Set<Object> userIds = redisTemplate.opsForZSet().reverseRangeByScore(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        assert userIds != null;
        if (userIds.size() == 0)
            return Response.notContent();
        return Response.ok(userService.getUsersBySet(userIds));
    }

    @Override
    public Response followUser(Long targetUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        createFollowUser(userUtil.getUser().getId(), targetUserId);
        return Response.ok();
    }

    @Override
    public Response unFollowUser(Long targetUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        deleteFollowUser(userUtil.getUser().getId(), targetUserId);
        return Response.ok();
    }
}
