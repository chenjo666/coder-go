package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.constants.NoticeTopic;
import com.cj.studycirclebackend.event.*;
import com.cj.studycirclebackend.service.LikeService;
import com.cj.studycirclebackend.dao.LikeMapper;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.pojo.Like;
import com.cj.studycirclebackend.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void createPostLike(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());

        // 点赞事件
        Event event = new LikePostEvent(NoticeTopic.LIKE, NoticeType.LIKE_POST.getValue(), postId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void createCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());

        // 点赞事件
        Event event = new LikeCommentEvent(NoticeTopic.LIKE, NoticeType.LIKE_COMMENT.getValue(), commentId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deletePostLike(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        redisTemplate.opsForZSet().remove(key, userId);
    }

    @Override
    public void deleteCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        redisTemplate.opsForZSet().remove(key, userId);
    }

    @Override
    public Long getPostLikeTotal(Long postId) {
        String key = RedisUtil.getPostLikeKey(postId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public Long getCommentLikeTotal(Long commentId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public boolean isLikePostByUser(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }

    @Override
    public boolean isLikeCommentByUser(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }
}
