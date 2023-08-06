package com.cj.codergobackend.controller;

import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    // v1 - 删除全部通知
    @DeleteMapping("/v1/notices")
    public Response deleteAllNotice() {
        return noticeService.deleteAllNotice();
    }
    // v1 - 删除单条通知
    @DeleteMapping("/v1/notices/{noticeId}")
    public Response deleteSingleNotice(@PathVariable Long noticeId) {
        return noticeService.deleteNotice(noticeId);
    }
    // v1 - 查找通知列表
    @GetMapping("/v1/notices")
    public Response getNotice(@CookieValue("token") String token, @RequestParam Integer page, @RequestParam Integer limit) {
        return noticeService.getNotice(page, limit);
    }
}
