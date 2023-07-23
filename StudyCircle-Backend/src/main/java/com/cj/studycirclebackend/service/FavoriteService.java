package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.Favorite;

public interface FavoriteService extends IService<Favorite> {
    void createPostFavorite(Long postId, Long userId);
    void deletePostFavorite(Long postId, Long userId);
    Long getPostFavoriteTotal(Long postId);
    boolean isFavoritePostByUser(Long postId, Long userId);
}
