package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Follow;

import java.util.Set;

public interface FollowService extends IService<Follow> {
    void createFollowUser(Long userFromId, Long userToId);
    void deleteFollowUser(Long userFromId, Long userToId);
    boolean isFollowedByUser(Long userFromId, Long userToId);

    Set<Object> getUserFollowersSet(Long userId);
    Set<Object> getUserFollowingsSet(Long userId);
    Response getUserFollowers(Long userId);
    Response getUserFollowings(Long userId);

    Response followUser(Long targetUserId);
    Response unFollowUser(Long targetUserId);
}
