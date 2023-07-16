package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Follow;

public interface FollowService extends IService<Follow> {

    Response createFollow(Long userId);

    Response deleteFollow(Long userId);

    boolean isFollowed(Long userFromId, Long userToId);

    Response getStars(Long userId);

    Response getFans(Long userId);
}
