package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.LikeMapper;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.CommentObjectType;
import com.example.studycirclebackend.enums.ResponseCode;
import com.example.studycirclebackend.enums.ResponseMsg;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.pojo.Like;
import com.example.studycirclebackend.service.LikeService;
import com.example.studycirclebackend.util.RedisUtil;
import com.example.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {
    @Resource
    private UserUtil userUtil;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Response createLike(Long objectId, String objectType) {
        if (objectId == null || StringUtils.isBlank(objectType)) {
            return Response.builder().badRequest().build();
        }
        Like like = new Like();
        like.setUserId(userUtil.getUser().getId());
        like.setObjectId(objectId);
        like.setObjectType(objectType);
        // 1）mysql
        save(like);

        // 点赞
        Event event = Event.builder()
                .topic("like")
                .userFromId(userUtil.getUser().getId())
                .objectId(objectId)
                .objectType(objectType)
                .build();;
        eventProducer.createEvent(event);

        return Response.builder().ok().build();
    }

    @Override
    public Response deleteLike(Long objectId, String objectType) {
        if (objectId == null || StringUtils.isBlank(objectType)) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        boolean result = remove(new QueryWrapper<Like>()
                .eq("object_id", objectId)
                .eq("object_type", objectType)
                .eq("user_id", userUtil.getUser().getId()));
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }




    /** 业务自身处理方法 **/
    // 查询某个实体（帖子、评论）的点赞数量
    @Override
    public Long getLikeCountByObject(Long objectId, String objectType) {
        if (objectId == null || StringUtils.isBlank(objectType)) {
            return null;
        }
        return count(new QueryWrapper<Like>().eq("object_id", objectId).eq("object_type", objectType));
    }
    // 查询某人是否点赞某个实体
    @Override
    public boolean isLikedByUser(Long userId, Long objectId, String objectType) {
        if (userId == null || objectId == null || StringUtils.isBlank(objectType)) {
            return false;
        }
        Like one = getOne(new QueryWrapper<Like>()
                .eq("user_id", userId)
                .eq("object_id", objectId)
                .eq("object_type", objectType));
        return one != null;
    }

    @Override
    public void createPostLike(Long postId, Long userId) {
        String key = RedisUtil.getPostLikeKey(postId);
        redisTemplate.opsForSet().add(key, userId);
    }

    @Override
    public void createCommentLike(Long commentId, Long userId) {
        String key = RedisUtil.getCommentLikeKey(commentId);
        redisTemplate.opsForSet().add(key, userId);
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
