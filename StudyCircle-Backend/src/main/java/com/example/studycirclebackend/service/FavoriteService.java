package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Favorite;

public interface FavoriteService extends IService<Favorite> {

    Response createFavorite(Long postId);

    Response deleteFavorite(Long postId);

    Long getFavoriteCountByPost(Long postId);

    boolean isFavorite(Long userId, Long postId);


}
