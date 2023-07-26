package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dao.FavoriteMapper;
import com.cj.studycirclebackend.constants.NoticeTopic;
import com.cj.studycirclebackend.service.FavoriteService;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.event.FavoritePostEvent;
import com.cj.studycirclebackend.event.Event;
import com.cj.studycirclebackend.event.EventProducer;
import com.cj.studycirclebackend.pojo.Favorite;
import com.cj.studycirclebackend.util.RedisUtil;
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
        // 收藏帖子的用户
        String key = RedisUtil.getPostFavoriteKey(postId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
        // 某人收藏的帖子
        String userKey = RedisUtil.getUserFavoriteKey(userId);
        redisTemplate.opsForZSet().add(userKey, postId, System.currentTimeMillis());

        // 收藏事件
        Event event = new FavoritePostEvent(NoticeTopic.FAVORITE, NoticeType.FAVORITE_POST.getValue(), postId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deletePostFavorite(Long postId, Long userId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        redisTemplate.opsForZSet().remove(key, userId);
        // 某人收藏的帖子
        String userFavoriteKey = RedisUtil.getUserFavoriteKey(userId);
        redisTemplate.opsForZSet().remove(userFavoriteKey, postId);

    }

    @Override
    public Long getPostFavoriteTotal(Long postId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public boolean isFavoritePostByUser(Long postId, Long userId) {
        String key = RedisUtil.getPostFavoriteKey(postId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }

}
