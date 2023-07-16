package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dao.BlockedLetterMapper;
import com.example.studycirclebackend.pojo.BlockedLetter;

public interface BlockedLetterService extends IService<BlockedLetter> {
    boolean createBlockedLetter(Long blockedUserId);
    boolean deleteBlockedLetter(Long blockedUserId);
}
