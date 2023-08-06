package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.dao.ArticleFavoriteMapper;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.event.ArticleFavoriteEvent;
import com.cj.codergobackend.service.ArticleFavoriteService;
import com.cj.codergobackend.event.Event;
import com.cj.codergobackend.event.EventProducer;
import com.cj.codergobackend.pojo.ArticleFavorite;
import com.cj.codergobackend.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class ArticleFavoriteServiceImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteService {
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void createArticleFavorite(Long articleId, Long userId) {
        // 收藏文章的用户
        String key = RedisUtil.getArticleFavoriteKey(articleId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
        // 某人收藏的文章
        String userKey = RedisUtil.getUserFavoriteKey(userId);
        redisTemplate.opsForZSet().add(userKey, articleId, System.currentTimeMillis());

        // 收藏事件
        Event event = new ArticleFavoriteEvent(NoticeTopic.ARTICLE_FAVORITE, NoticeType.ARTICLE_FAVORITE, articleId, userId);
        eventProducer.createEvent(event);
    }

    @Override
    public void deleteArticleFavorite(Long articleId, Long userId) {
        String key = RedisUtil.getArticleFavoriteKey(articleId);
        redisTemplate.opsForZSet().remove(key, userId);
        // 某人收藏的文章
        String userFavoriteKey = RedisUtil.getUserFavoriteKey(userId);
        redisTemplate.opsForZSet().remove(userFavoriteKey, articleId);

    }

    @Override
    public Long getArticleFavoriteTotal(Long articleId) {
        String key = RedisUtil.getArticleFavoriteKey(articleId);
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public boolean isFavoriteArticleByUser(Long articleId, Long userId) {
        String key = RedisUtil.getArticleFavoriteKey(articleId);
        return null != redisTemplate.opsForZSet().rank(key, userId);
    }



}
