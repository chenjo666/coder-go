package com.cj.studycirclebackend.controller;

import com.cj.studycirclebackend.service.LetterService;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.service.BlockedLetterService;
import com.cj.studycirclebackend.util.DataUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/private_chat")
public class PrivateChatController {
    @Resource
    private LetterService letterService;
    @Resource
    private BlockedLetterService blockedLetterService;

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
        return blockedLetterService.createBlockedLetter(blockedUserId);
    }

    // v1 - 取消屏蔽私信
    @DeleteMapping("/v1/letters/blocks/{blockedUserId}")
    public Response deleteBlockedLetter(@PathVariable("blockedUserId") Long blockedUserId) {
        return blockedLetterService.deleteBlockedLetter(blockedUserId);
    }

}
