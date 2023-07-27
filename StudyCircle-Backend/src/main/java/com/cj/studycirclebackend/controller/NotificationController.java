package com.cj.studycirclebackend.controller;

import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

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
