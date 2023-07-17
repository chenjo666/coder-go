package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.FavoriteMapper;
import com.example.studycirclebackend.enums.NoticeType;
import com.example.studycirclebackend.event.FavoritePostEvent;
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
    public void createPostFavorite(Long postId, Long userId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        redisTemplate.opsForSet().add(key, userId);

        // 收藏事件
        Event event = new FavoritePostEvent(Topic.FAVORITE, NoticeType.FAVORITE_POST.getValue(), postId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deletePostFavorite(Long postId, Long userId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        redisTemplate.opsForSet().remove(key, userId);
    }

    @Override
    public Long getPostFavoriteTotal(Long postId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public boolean isFavoritePostByUser(Long postId, Long userId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }

}
