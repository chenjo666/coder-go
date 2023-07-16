package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Like;

public interface LikeService extends IService<Like> {

    Response createLike(Long objectId, String objectType);

    Response deleteLike(Long objectId, String objectType);

    Long getLikeCountByObject(Long objectId, String objectType);

    boolean isLikedByUser(Long userId, Long objectId, String objectType);
}
