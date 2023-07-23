package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.event.Topic;
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
        String followingKey = RedisUtil.getUserFOLLOWINGKey(userFromId);
        redisTemplate.opsForSet().add(followingKey, userToId);
        String followerKey = RedisUtil.getUserFollowerKey(userToId);
        redisTemplate.opsForSet().add(followerKey, userFromId);

        // 关注事件
        Event followEvent = new FollowUserEvent(Topic.FOLLOW, NoticeType.FOLLOW_USER.getValue(), userFromId, userToId);
        eventProducer.createEvent(followEvent);
    }

    @Override
    public void deleteFollowUser(Long userFromId, Long userToId) {
        String followingKey = RedisUtil.getUserFOLLOWINGKey(userFromId);
        redisTemplate.opsForSet().remove(followingKey, userToId);
        String followerKey = RedisUtil.getUserFollowerKey(userToId);
        redisTemplate.opsForSet().remove(followerKey, userFromId);
    }

    @Override
    public boolean isFollowedByUser(Long userFromId, Long userToId) {
        String key = RedisUtil.getUserFOLLOWINGKey(userFromId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userToId));
    }

    @Override
    public Set<Object> getUserFollowers(Long userId) {
        String key = RedisUtil.getUserFollowerKey(userId);
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Set<Object> getUserFollowings(Long userId) {
        String key = RedisUtil.getUserFOLLOWINGKey(userId);
        return redisTemplate.opsForSet().members(key);
    }
}
