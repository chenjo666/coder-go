package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.service.FollowService;
import com.cj.codergobackend.service.LetterBlockedService;
import com.cj.codergobackend.service.LetterService;
import com.cj.codergobackend.dao.LetterMapper;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.LetterBlocked;
import com.cj.codergobackend.pojo.Letter;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import com.cj.codergobackend.util.UserUtil;
import com.cj.codergobackend.vo.LetterDetailVO;
import com.cj.codergobackend.vo.LetterOverviewVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LetterServiceImpl extends ServiceImpl<LetterMapper, Letter> implements LetterService {
    @Resource
    private UserUtil userUtil;
    @Resource
    private UserService userService;
    @Resource
    private FollowService followService;
    @Resource
    private LetterBlockedService letterBlockedService;
    private static final Logger logger = LoggerFactory.getLogger(LetterServiceImpl.class);

    @Override
    @Transactional
    public Response getLetterList(String token) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        Long userId = userUtil.getUser().getId();
        // 1. 查询出对话人
        List<Long> toUserIds = getBaseMapper().getToUserIds(userId);
        if (toUserIds == null) {
            return Response.notContent();
        }
        // 2. 查询出对话记录
        List<LetterOverviewVO> letterOverviewVOList = new ArrayList<>(toUserIds.size());
        for (Long toUserId : toUserIds) {
            letterOverviewVOList.add(getLetterOverviewVO(toUserId, userId));
        }
        letterOverviewVOList.get(0).setTotalUnread(0);
        // 3. 查询出第一条详细的私信信息
        List<LetterDetailVO> letterDetailVOList = getLetterDetails(userId, letterOverviewVOList.get(0).getUserId());
        if (letterDetailVOList == null) {
            return Response.notContent();
        }
        // 4. 封装结果
        Map<String, Object> data = new HashMap<>();
        data.put("letterOverviewVOList", letterOverviewVOList);
        data.put("letterDetailVOList", letterDetailVOList);
        return Response.ok(data);
    }

    @Override
    public Response getLetterDetails(Long userId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        List<LetterDetailVO> res = getLetterDetails(userUtil.getUser().getId(), userId);
        return res != null ? Response.ok(res) : Response.notContent();
    }

    @Override
    public Response createLetter(Long userId, String content, Date sendTime) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        Letter letter = Letter.builder()
                .userFromId(userUtil.getUser().getId())
                .userToId(userId)
                .content(content)
                .createdAt(sendTime)
                .build();
        boolean res = save(letter);
        return res ? Response.created() : Response.internalServerError();
    }

    @Override
    public Response deleteLetters(Long toUserId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        boolean delFrom = update(new UpdateWrapper<Letter>()
                .set("is_deleted_from_user", 1)
                .eq("user_from_id", userUtil.getUser().getId())
                .eq("user_to_id", toUserId));
        boolean delTo = update(new UpdateWrapper<Letter>()
                .set("is_deleted_to_user", 1)
                .eq("user_from_id", toUserId)
                .eq("user_to_id", userUtil.getUser().getId()));
        if (!delFrom && !delTo) {
            return Response.notContent();
        }
        return Response.ok();
    }


    @Override
    public Letter saveLetter(Long userFromId, Long userToId, String content, String time) {
        Letter letter = Letter.builder()
                .userFromId(userFromId)
                .userToId(userToId)
                .content(content)
                .createdAt(DataUtil.formatStringToDate(time))
                .build();
        boolean res = save(letter);
        return res ? letter : null;
    }

    @Override
    public LetterOverviewVO getLetterOverviewVO(Long fromUserId, Long toUserId) {
        LetterOverviewVO letterOverviewVO = new LetterOverviewVO();
        // 查询未读数量
        long count = count(new QueryWrapper<Letter>()
                .eq("user_from_id", fromUserId)
                .eq("user_to_id", toUserId)
                .eq("is_read", 0));
        letterOverviewVO.setTotalUnread(Math.toIntExact(count));
        // 查询头像、姓名
        User user = userService.getById(fromUserId);
        letterOverviewVO.setUserId(fromUserId);
        letterOverviewVO.setUserName(user.getUsername());
        letterOverviewVO.setUserAvatar(user.getAvatar());
        // 查询最新消息、最新内容
        Letter newestLetter = getOne(new QueryWrapper<Letter>()
                .eq("user_from_id", fromUserId)
                .eq("user_to_id", toUserId)
                .eq("is_deleted_from_user", 0)
                .or()
                .eq("user_to_id", fromUserId)
                .eq("user_from_id", toUserId)
                .eq("is_deleted_to_user", 0)
                .orderByDesc("created_at")
                .last(String.format("LIMIT %d", 1)));
        letterOverviewVO.setNewContent(newestLetter.getContent());
        letterOverviewVO.setNewDate(DataUtil.formatDateTime(newestLetter.getCreatedAt()));
        // 查询关注、粉丝、是否屏蔽
        boolean isStar = followService.isFollowedByUser(toUserId, fromUserId);
        boolean isFan = followService.isFollowedByUser(fromUserId, toUserId);
        letterOverviewVO.setFan(isFan);
        letterOverviewVO.setStar(isStar);
        LetterBlocked one = letterBlockedService.getOne(new QueryWrapper<LetterBlocked>()
                .eq("user_id", toUserId)
                .eq("blocked_user_id", user.getId()));
        letterOverviewVO.setBlock(one != null);

        return letterOverviewVO;
    }

    // 查询出某个私信全部会话记录，并更新为已读
    @Override
    @Transactional
    public List<LetterDetailVO> getLetterDetails(Long fromUserId, Long toUserId) {
        List<LetterDetailVO> letterDetailVOList = new ArrayList<>();
        List<Letter> list = list(new QueryWrapper<Letter>()
                .eq("is_deleted_from_user", 0)
                .eq("user_from_id", fromUserId)
                .eq("user_to_id", toUserId)
                .or()
                .eq("is_deleted_to_user", 0)
                .eq("user_to_id", fromUserId)
                .eq("user_from_id", toUserId)
                .orderByAsc("created_at"));
        if (list == null) {
            return null;
        }
        for (Letter letter : list) {
            letterDetailVOList.add(getLetterDetailVO(letter));
            letter.setIsRead(1);
        }
        boolean res = updateBatchById(list);
        return res ? letterDetailVOList : null;
    }

    public LetterDetailVO getLetterDetailVO(Letter letter) {
        Long id = userUtil.getUser().getId();
        LetterDetailVO letterDetailVO = new LetterDetailVO();
        letterDetailVO.setSelf(Objects.equals(letter.getUserFromId(), id));
        letterDetailVO.setCreatedAt(DataUtil.formatDateTime(letter.getCreatedAt()));
        letterDetailVO.setContent(letter.getContent());
        return letterDetailVO;
    }

}
