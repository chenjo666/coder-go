package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.ResponseCode;
import com.example.studycirclebackend.service.BlockedLetterService;
import com.example.studycirclebackend.service.LetterService;
import com.example.studycirclebackend.util.DataUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/letters")
public class LetterController {
    @Resource
    private LetterService letterService;
    @Resource
    private BlockedLetterService blockedLetterService;

    @GetMapping
    public Response getLetterList(@CookieValue("token") String token) {
        if (StringUtils.isBlank(token)) {
            return Response.builder().code(-1).msg("请先登录！").build();
        }
        return letterService.getLetterList(token);
    }

    @GetMapping("/{userId}/messages")
    public Response getLetterDetails(@PathVariable("userId") Long userId) {
        return letterService.getLetterDetails(userId);
    }

    @PostMapping
    public Response createLetter(@RequestBody Map<String, String> args) {
        Long userId = Long.valueOf(args.get("userId"));
        String content = args.get("content");
        Date date = DataUtil.formatStringToDate(args.get("sendTime"));
        return letterService.createLetter(userId, content, date);
    }

    @DeleteMapping("/{userId}")
    public Response deleteLetters(@PathVariable("userId") Long userId) {
        return letterService.deleteLetters(userId);
    }

    /**
     * 添加屏蔽私信
     * @param blockedUserId
     * @return
     */
    @PostMapping("/blocks/{blockedUserId}")
    public Response createBlockedLetter(@PathVariable("blockedUserId") Long blockedUserId) {
        return Response.builder()
                .code(blockedLetterService.createBlockedLetter(blockedUserId) ?
                        ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    /**
     * 取消屏蔽私信
     * @param blockedUserId
     * @return
     */
    @DeleteMapping("/blocks/{blockedUserId}")
    public Response deleteBlockedLetter(@PathVariable("blockedUserId") Long blockedUserId) {
        return Response.builder()
                .code(blockedLetterService.deleteBlockedLetter(blockedUserId) ?
                        ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

}
