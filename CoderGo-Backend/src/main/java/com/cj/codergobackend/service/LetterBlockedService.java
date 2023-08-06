package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.LetterBlocked;

public interface LetterBlockedService extends IService<LetterBlocked> {
    Response createLetterBlocked(Long blockedUserId);
    Response deleteLetterBlocked(Long blockedUserId);
}
