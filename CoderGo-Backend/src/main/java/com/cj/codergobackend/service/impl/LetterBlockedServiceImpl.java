package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.dao.LetterBlockedMapper;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.LetterBlocked;
import com.cj.codergobackend.service.LetterBlockedService;
import com.cj.codergobackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LetterBlockedServiceImpl extends ServiceImpl<LetterBlockedMapper, LetterBlocked> implements LetterBlockedService {

    @Resource
    private UserUtil userUtil;
    @Override
    public Response createLetterBlocked(Long blockedUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        LetterBlocked LetterBlocked = new LetterBlocked();
        LetterBlocked.setUserId(userUtil.getUser().getId());
        LetterBlocked.setBlockedUserId(blockedUserId);
        boolean res = save(LetterBlocked);
        return res ? Response.created() : Response.internalServerError();
    }

    @Override
    public Response deleteLetterBlocked(Long blockedUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        boolean res = remove(new QueryWrapper<LetterBlocked>()
                .eq("user_id", userUtil.getUser().getId())
                .eq("blocked_user_id", blockedUserId));
        return res ? Response.notContent() : Response.notFound();
    }
}
