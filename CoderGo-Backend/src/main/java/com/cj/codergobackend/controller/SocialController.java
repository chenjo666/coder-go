package com.cj.codergobackend.controller;

import com.cj.codergobackend.service.FollowService;
import com.cj.codergobackend.service.LetterBlockedService;
import com.cj.codergobackend.service.LetterService;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.util.DataUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/social")
public class SocialController {
    @Resource
    private LetterService letterService;
    @Resource
    private LetterBlockedService blockedLetterService;
    @Resource
    private FollowService followService;

    // v1 - 得到某人的全部私信
    @GetMapping("/v1/letters")
    public Response getLetterList(@CookieValue("token") String token) {
        return letterService.getLetterList(token);
    }
    // v1 - 得到某人与某人的私信记录
    @GetMapping("/v1/letters/{toUserId}")
    public Response getLetterDetails(@PathVariable("toUserId") Long toUserId) {
        return letterService.getLetterDetails(toUserId);
    }
    // v1 - 创建一条私信
    @PostMapping("/v1/letters")
    public Response createLetter(@RequestBody Map<String, String> args) {
        Long userId = Long.valueOf(args.get("toUserId"));
        String content = args.get("content");
        Date date = DataUtil.formatStringToDate(args.get("sendTime"));
        return letterService.createLetter(userId, content, date);
    }
    // v1 - 删除与某人的私信记录
    @DeleteMapping("/v1/letters/{toUserId}")
    public Response deleteLetters(@PathVariable("toUserId") Long toUserId) {
        return letterService.deleteLetters(toUserId);
    }

    // v1 - 添加屏蔽私信
    @PostMapping("/v1/letters/blocks/{blockedUserId}")
    public Response createBlockedLetter(@PathVariable("blockedUserId") Long blockedUserId) {
        return blockedLetterService.createLetterBlocked(blockedUserId);
    }

    // v1 - 取消屏蔽私信
    @DeleteMapping("/v1/letters/blocks/{blockedUserId}")
    public Response deleteBlockedLetter(@PathVariable("blockedUserId") Long blockedUserId) {
        return blockedLetterService.deleteLetterBlocked(blockedUserId);
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
        return followService.getUserFollowings(userId);
    }
    // v1 - 用户的粉丝列表
    @GetMapping("/v1/{userId}/followers")
    public Response getFollowers(@PathVariable("userId") Long userId) {
        return followService.getUserFollowers(userId);
    }

}
