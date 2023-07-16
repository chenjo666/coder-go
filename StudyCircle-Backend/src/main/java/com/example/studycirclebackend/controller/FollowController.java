package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.FollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class FollowController {
    @Resource
    private FollowService followService;

    /**
     * 关注
     */
    @PostMapping("/following/{target_user_id}")
    public Response createFollow(@PathVariable("target_user_id") Long targetUserId) {
        return followService.createFollow(targetUserId);
    }
    /**
     * 取消关注
     */
    @DeleteMapping("/following/{target_user_id}")
    public Response deleteFollow(@PathVariable("target_user_id") Long targetUserId) {
        return followService.deleteFollow(targetUserId);
    }
    /**
     * 得到某人的关注列表
     */
    @GetMapping("/{user_id}/following")
    public Response getStars(@PathVariable("user_id") Long userId) {
        return followService.getStars(userId);
    }
    /**
     * 得到某人的粉丝列表
     */
    @GetMapping("/{user_id}/followers")
    public Response getFans(@PathVariable("user_id") Long userId) {
        return followService.getFans(userId);
    }
}
