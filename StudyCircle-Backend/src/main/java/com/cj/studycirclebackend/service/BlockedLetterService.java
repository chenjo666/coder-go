package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.BlockedLetter;

public interface BlockedLetterService extends IService<BlockedLetter> {
    boolean createBlockedLetter(Long blockedUserId);
    boolean deleteBlockedLetter(Long blockedUserId);
}
