package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.BlockedLetter;

public interface BlockedLetterService extends IService<BlockedLetter> {
    Response createBlockedLetter(Long blockedUserId);
    Response deleteBlockedLetter(Long blockedUserId);
}
