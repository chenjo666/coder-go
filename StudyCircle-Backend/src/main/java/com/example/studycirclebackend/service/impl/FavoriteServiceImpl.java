package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.FavoriteMapper;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.ResponseCode;
import com.example.studycirclebackend.enums.ResponseMsg;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.pojo.Favorite;
import com.example.studycirclebackend.service.FavoriteService;
import com.example.studycirclebackend.util.RedisUtil;
import com.example.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    @Resource
    private UserUtil userUtil;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Response createFavorite(Long postId) {
        if (postId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userUtil.getUser().getId());
        favorite.setPostId(postId);

        // 收藏事件
        Event event = Event.builder()
                .topic("favorite")
                .userFromId(userUtil.getUser().getId())
                .postId(postId)
                .build();
        eventProducer.createEvent(event);

        boolean result = save(favorite);
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response deleteFavorite(Long postId) {
        if (postId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        boolean result = remove(new QueryWrapper<Favorite>()
                .eq("user_id", userUtil.getUser().getId())
                .eq("post_id", postId));

        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Long getFavoriteCountByPost(Long postId) {
        if (postId == null) {
            return 0L;
        }
        return count(new QueryWrapper<Favorite>().eq("post_id", postId));
    }

    @Override
    public boolean isFavorite(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return getOne(new QueryWrapper<Favorite>().eq("user_id", userId).eq("post_id",postId)) != null;
    }

    @Override
    public void createPostCollect(Long postId, Long userId) {
        String key = RedisUtil.getPostCollectKey(postId);
        redisTemplate.opsForSet().add(key, userId);
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
