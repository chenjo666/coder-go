package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.event.*;
import com.cj.codergobackend.service.LikeService;
import com.cj.codergobackend.dao.LikeMapper;
import com.cj.codergobackend.pojo.Like;
import com.cj.codergobackend.util.RedisUtil;
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
    public void createArticleLike(Long articleId, Long userId) {
        String key = RedisUtil.getArticleLikeKey(articleId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());

        // 点赞事件
        Event event = new ArticleLikeEvent(NoticeTopic.ARTICLE_LIKE, NoticeType.ARTICLE_LIKE, articleId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void createArticleCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getArticleCommentLikeKey(commentId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());

        // 点赞事件
        Event event = new ArticleCommentLikeEvent(NoticeTopic.ARTICLE_COMMENT_LIKE, NoticeType.ARTICLE_COMMENT_LIKE, commentId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deleteArticleLike(Long articleId, Long userId) {
        String key = RedisUtil.getArticleLikeKey(articleId);
        redisTemplate.opsForZSet().remove(key, userId);
    }

    @Override
    public void deleteArticleCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getArticleCommentLikeKey(commentId);
        redisTemplate.opsForZSet().remove(key, userId);
    }

    @Override
    public Long getArticleLikeTotal(Long articleId) {
        String key = RedisUtil.getArticleLikeKey(articleId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public Long getArticleCommentLikeTotal(Long commentId) {
        String key = RedisUtil.getArticleCommentLikeKey(commentId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public boolean isLikeArticleByUser(Long articleId, Long userId) {
        String key = RedisUtil.getArticleLikeKey(articleId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }

    @Override
    public boolean isLikeArticleCommentByUser(Long commentId, Long userId) {
        String key = RedisUtil.getArticleCommentLikeKey(commentId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }
}
