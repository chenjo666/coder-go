package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.pojo.Like;

public interface LikeService extends IService<Like> {


    void createArticleLike(Long articleId, Long userId);
    void createArticleCommentLike(Long commentId, Long userId);

    void deleteArticleLike(Long articleId, Long userId);
    void deleteArticleCommentLike(Long commentId, Long userId);

    Long getArticleLikeTotal(Long articleId);
    Long getArticleCommentLikeTotal(Long commentId);

    boolean isLikeArticleByUser(Long articleId, Long userId);
    boolean isLikeArticleCommentByUser(Long commentId, Long userId);
}
