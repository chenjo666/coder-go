package com.cj.studycirclebackend.controller;

import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.service.FollowService;
import com.cj.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private FollowService followService;

    // v1 - 用户登录
    @PostMapping("/v1/login")
    public Response login(String email, String password, HttpServletResponse response) {
        return userService.login(email, password, response);
    }
    // v1 - 用户登出
    @DeleteMapping("/v1/logout")
    public Response logout(@CookieValue("token") String token) {
        return userService.logout(token);
    }

    // v1 - 用户激活
    @PostMapping("/v1/activation")
    public Response activation(String email) {
        return userService.activate(email);
    }

    // v1 - 用户注册
    @PostMapping("/v1/register")
    public Response register(String email, String password, String code, HttpServletResponse response) {
        return userService.register(email, password, code, response);
    }

    // v1 - 关注用户
    @PostMapping("/v1/following/{targetUserId}")
    public Response createFollow(@PathVariable("targetUserId") Long targetUserId) {
        return followService.followUser(targetUserId);
    }

    // v1 - 取消关注用户
    @DeleteMapping("/v1/following/{targetUserId}")
    public Response deleteFollow(@PathVariable("targetUserId") Long targetUserId) {
        return followService.unFollowUser(targetUserId);
    }
    // v1 - 用户的关注列表
    @GetMapping("/v1/{userId}/followings")
    public Response getFollowings(@PathVariable("userId") Long userId) {
        return userService.getUserFollowings(userId);
    }
    // v1 - 用户的粉丝列表
    @GetMapping("/v1/{userId}/followers")
    public Response getFollowers(@PathVariable("userId") Long userId) {
        return userService.getUserFollowers(userId);
    }
}
