package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Like;

public interface LikeService extends IService<Like> {

    Response createLike(Long objectId, String objectType);

    Response deleteLike(Long objectId, String objectType);

    Long getLikeCountByObject(Long objectId, String objectType);

    boolean isLikedByUser(Long userId, Long objectId, String objectType);


    void createPostLike(Long postId, Long userId);
    void createCommentLike(Long commentId, Long userId);

    void deletePostLike(Long postId, Long userId);
    void deleteCommentLike(Long commentId, Long userId);

    Long getPostLikeTotal(Long postId);
    Long getCommentLikeTotal(Long commentId);

    boolean isLikePostByUser(Long postId, Long userId);
    boolean isLikeCommentByUser(Long commentId, Long userId);
}
