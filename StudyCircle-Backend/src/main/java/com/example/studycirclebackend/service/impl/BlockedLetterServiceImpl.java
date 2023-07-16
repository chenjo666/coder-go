package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.BlockedLetterMapper;
import com.example.studycirclebackend.pojo.BlockedLetter;
import com.example.studycirclebackend.service.BlockedLetterService;
import com.example.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class BlockedLetterServiceImpl extends ServiceImpl<BlockedLetterMapper, BlockedLetter> implements BlockedLetterService {

    @Resource
    private UserUtil userUtil;
    @Override
    public boolean createBlockedLetter(Long blockedUserId) {
        if (blockedUserId == null) {
            return false;
        }
        BlockedLetter blockedLetter = new BlockedLetter();
        blockedLetter.setUserId(userUtil.getUser().getId());
        blockedLetter.setBlockedUserId(blockedUserId);
        return save(blockedLetter);
    }


    @Override
    public boolean deleteBlockedLetter(Long blockedUserId) {
        if (blockedUserId == null) {
            return false;
        }
        return remove(new QueryWrapper<BlockedLetter>()
                .eq("user_id", userUtil.getUser().getId())
                .eq("blocked_user_id", blockedUserId));
    }
}
