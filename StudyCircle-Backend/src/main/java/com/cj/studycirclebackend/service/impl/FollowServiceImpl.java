package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.constants.NoticeTopic;
import com.cj.studycirclebackend.service.FollowService;
import com.cj.studycirclebackend.dao.FollowMapper;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.event.Event;
import com.cj.studycirclebackend.event.EventProducer;
import com.cj.studycirclebackend.event.FollowUserEvent;
import com.cj.studycirclebackend.pojo.Follow;
import com.cj.studycirclebackend.util.RedisUtil;
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

    @Override
    public void createFollowUser(Long userFromId, Long userToId) {
        String followingKey = RedisUtil.getUserFollowingKey(userFromId);
        redisTemplate.opsForZSet().add(followingKey, userToId, System.currentTimeMillis());
        String followerKey = RedisUtil.getUserFollowerKey(userToId);
        redisTemplate.opsForZSet().add(followerKey, userFromId, System.currentTimeMillis());

        // 关注事件
        Event followEvent = new FollowUserEvent(NoticeTopic.FOLLOW, NoticeType.FOLLOW_USER.getValue(), userFromId, userToId);
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
    public Set<Object> getUserFollowers(Long userId) {
        String key = RedisUtil.getUserFollowerKey(userId);
        return redisTemplate.opsForZSet().reverseRangeByScore(key, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    @Override
    public Set<Object> getUserFollowings(Long userId) {
        String key = RedisUtil.getUserFollowingKey(userId);
        return redisTemplate.opsForZSet().reverseRangeByScore(key, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }
}
