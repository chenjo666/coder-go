package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.FavoriteMapper;
import com.example.studycirclebackend.enums.NoticeType;
import com.example.studycirclebackend.event.CollectPostEvent;
import com.example.studycirclebackend.event.Event;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.event.Topic;
import com.example.studycirclebackend.pojo.Favorite;
import com.example.studycirclebackend.service.FavoriteService;
import com.example.studycirclebackend.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void createPostCollect(Long postId, Long userId) {
        String key = RedisUtil.getPostCollectKey(postId);
        redisTemplate.opsForSet().add(key, userId);

        // 收藏事件
        Event event = new CollectPostEvent(Topic.COLLECT, NoticeType.FAVORITE_POST.getValue(), postId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deletePostCollect(Long postId, Long userId) {
        String key = RedisUtil.getPostCollectKey(postId);
        redisTemplate.opsForSet().remove(key, userId);
    }

    @Override
    public Long getPostCollectTotal(Long postId) {
        String key = RedisUtil.getPostCollectKey(postId);
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public boolean isCollectPostByUser(Long postId, Long userId) {
        String key = RedisUtil.getPostCollectKey(postId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }

}
