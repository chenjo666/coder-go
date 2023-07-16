package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 删除全部通知
     * @return
     */
    @DeleteMapping
    public Response deleteAllNotice() {
        return Response.builder()
                .code(noticeService.deleteAllNotice() ? 200 : -1)
                .build();
    }
    /**
     * 删除单条通知
     * @param noticeId
     * @return
     */
    @DeleteMapping("/{noticeId}")
    public Response deleteSingleNotice(@PathVariable Long noticeId) {
        return Response.builder()
                .code(noticeService.deleteSingleNotice(noticeId) ? 200 : -1)
                .build();
    }
    /**
     * 获取通知
     */
    @GetMapping
    public Response getNotice(@RequestParam Integer page, @RequestParam Integer limit) {
        return noticeService.getNotice(page, limit);
    }
}
