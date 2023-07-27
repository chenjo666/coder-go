package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dao.BlockedLetterMapper;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.BlockedLetter;
import com.cj.studycirclebackend.service.BlockedLetterService;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class BlockedLetterServiceImpl extends ServiceImpl<BlockedLetterMapper, BlockedLetter> implements BlockedLetterService {

    @Resource
    private UserUtil userUtil;
    @Override
    public Response createBlockedLetter(Long blockedUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        BlockedLetter blockedLetter = new BlockedLetter();
        blockedLetter.setUserId(userUtil.getUser().getId());
        blockedLetter.setBlockedUserId(blockedUserId);
        boolean res = save(blockedLetter);
        return res ? Response.created() : Response.internalServerError();
    }

    @Override
    public Response deleteBlockedLetter(Long blockedUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        boolean res = remove(new QueryWrapper<BlockedLetter>()
                .eq("user_id", userUtil.getUser().getId())
                .eq("blocked_user_id", blockedUserId));
        return res ? Response.notContent() : Response.notFound();
    }
}
