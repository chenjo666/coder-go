package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        redisTemplate.opsForSet().add(key, userId);

        // 点赞事件
        Event event = new LikePostEvent(Topic.LIKE, NoticeType.LIKE_POST.getValue(), postId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void createCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        redisTemplate.opsForSet().add(key, userId);

        // 点赞事件
        Event event = new LikeCommentEvent(Topic.LIKE, NoticeType.LIKE_COMMENT.getValue(), commentId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deletePostLike(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        redisTemplate.opsForSet().remove(key, userId);
    }

    @Override
    public void deleteCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        redisTemplate.opsForSet().remove(key, userId);
    }

    @Override
    public Long getPostLikeTotal(Long postId) {
        String key = RedisUtil.getPostLikeKey(postId);
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long getCommentLikeTotal(Long commentId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public boolean isLikePostByUser(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }

    @Override
    public boolean isLikeCommentByUser(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }
}
