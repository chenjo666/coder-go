package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.pojo.ArticleFavorite;

public interface ArticleFavoriteService extends IService<ArticleFavorite> {
    void createArticleFavorite(Long postId, Long userId);
    void deleteArticleFavorite(Long postId, Long userId);
    Long getArticleFavoriteTotal(Long postId);
    boolean isFavoriteArticleByUser(Long postId, Long userId);

}
