package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Follow;

import java.util.Set;

public interface FollowService extends IService<Follow> {

    Response createFollow(Long userId);

    Response deleteFollow(Long userId);

    boolean isFollowed(Long userFromId, Long userToId);

    Response getStars(Long userId);

    Response getFans(Long userId);


    void createFollow(Long userFromId, Long userToId);
    void deleteFollow(Long userFromId, Long userToId);
    boolean isFollowedByUser(Long userFromId, Long userToId);
    Set<Object> getUserFollowers(Long userId);
    Set<Object> getUserFollowings(Long userId);
}
