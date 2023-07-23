package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.Like;

public interface LikeService extends IService<Like> {


    void createPostLike(Long postId, Long userId);
    void createCommentLike(Long commentId, Long userId);

    void deletePostLike(Long postId, Long userId);
    void deleteCommentLike(Long commentId, Long userId);

    Long getPostLikeTotal(Long postId);
    Long getCommentLikeTotal(Long commentId);

    boolean isLikePostByUser(Long postId, Long userId);
    boolean isLikeCommentByUser(Long commentId, Long userId);
}
