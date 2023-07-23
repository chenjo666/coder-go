package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.Follow;

import java.util.Set;

public interface FollowService extends IService<Follow> {
    void createFollowUser(Long userFromId, Long userToId);
    void deleteFollowUser(Long userFromId, Long userToId);
    boolean isFollowedByUser(Long userFromId, Long userToId);
    Set<Object> getUserFollowers(Long userId);
    Set<Object> getUserFollowings(Long userId);
}
