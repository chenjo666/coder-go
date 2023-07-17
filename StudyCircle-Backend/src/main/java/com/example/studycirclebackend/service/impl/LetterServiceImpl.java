package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.LetterMapper;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.BlockedLetter;
import com.example.studycirclebackend.pojo.Letter;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.websocket.LetterRequest;
import com.example.studycirclebackend.service.BlockedLetterService;
import com.example.studycirclebackend.service.FollowService;
import com.example.studycirclebackend.service.LetterService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.DataUtil;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.LetterDetailVO;
import com.example.studycirclebackend.vo.LetterOverviewVO;
import jakarta.annotation.Resource;
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
    private BlockedLetterService blockedLetterService;


    @Override
    @Transactional
    public Response getLetterList(String token) {
        Long userId = userUtil.getUser().getId();
        // 1. 查询出全部的私信列表
        List<Letter> letters = list(new QueryWrapper<Letter>()
                .select("user_from_id")
                .eq("user_to_id", userId)
                .groupBy("user_from_id"));
        // 2. 查询未读数量，最新消息
        List<LetterOverviewVO> letterOverviewVOList = new ArrayList<>();
        for (Letter letter : letters) {
            LetterOverviewVO letterOverviewVO = new LetterOverviewVO();
            Long userFromId = letter.getUserFromId();
            // 查询未读数量
            Long count = count(new QueryWrapper<Letter>()
                    .eq("user_from_id", userFromId)
                    .eq("user_to_id", userId)
                    .eq("is_read", 0));
            letterOverviewVO.setUnReadCount(Math.toIntExact(count));
            // 查询头像、姓名
            User user = userService.getById(userFromId);
            letterOverviewVO.setUserId(userFromId.toString());
            letterOverviewVO.setUserName(user.getUsername());
            letterOverviewVO.setUserAvatar(user.getAvatar());
            // 查询最新消息、最新内容
            List<Letter> list = list(new QueryWrapper<Letter>()
                    .eq("user_from_id", userFromId)
                    .eq("user_to_id", userId)
                    .or()
                    .eq("user_to_id", userFromId)
                    .eq("user_from_id", userId)
                    .orderByDesc("send_time"));
            letterOverviewVO.setNewContent(list.get(0).getContent());
            letterOverviewVO.setNewDate(DataUtil.formatDateTime(list.get(0).getSendTime()));
            // 查询关注、粉丝、是否屏蔽
            boolean isStar = followService.isFollowedByUser(userId, userFromId);
            boolean isFan = followService.isFollowedByUser(userFromId, userId);
            letterOverviewVO.setFan(isFan);
            letterOverviewVO.setStar(isStar);
            BlockedLetter one = blockedLetterService.getOne(new QueryWrapper<BlockedLetter>()
                    .eq("user_id", userId)
                    .eq("blocked_user_id", user.getId()));
            letterOverviewVO.setBlock(one != null);

            letterOverviewVOList.add(letterOverviewVO);
        }
        letterOverviewVOList = letterOverviewVOList.stream()
                .sorted(Comparator.comparing(LetterOverviewVO::getNewDate).reversed()).toList();
        letterOverviewVOList.get(0).setUnReadCount(0);
        // 3. 查询出详细的私信信息
        List<LetterDetailVO> letterDetailVOList = new ArrayList<>();
        List<Letter> list = list(new QueryWrapper<Letter>()
                .eq("user_from_id", letterOverviewVOList.get(0).getUserId())
                .eq("user_to_id", userId)
                .or()
                .eq("user_to_id", letterOverviewVOList.get(0).getUserId())
                .eq("user_from_id", userId)
                .orderByAsc("send_time"));
        for (Letter letter : list) {
            LetterDetailVO letterDetailVO = convertToLetterDetailVO(letter);
            letterDetailVOList.add(letterDetailVO);
            letter.setIsRead(1);
        }
        updateBatchById(list);
        Map<String, Object> data = new HashMap<>();
        data.put("letterOverviewVOList", letterOverviewVOList);
        data.put("letterDetailVOList", letterDetailVOList);
        return Response.builder().code(200).data(data).build();
    }

    @Override
    @Transactional
    public Response getLetterDetails(Long userId) {
        Long id = userUtil.getUser().getId();
        List<LetterDetailVO> letterDetailVOList = new ArrayList<>();
        List<Letter> list = list(new QueryWrapper<Letter>()
                .eq("user_from_id", id)
                .eq("user_to_id", userId)
                .or()
                .eq("user_to_id", id)
                .eq("user_from_id", userId)
                .orderByAsc("send_time"));
        for (Letter letter : list) {
            LetterDetailVO letterDetailVO = convertToLetterDetailVO(letter);
            letterDetailVOList.add(letterDetailVO);
            letter.setIsRead(1);
        }
        updateBatchById(list);
        return Response.builder().code(200).data(letterDetailVOList).build();
    }

    @Override
    public Response createLetter(Long userId, String content, Date sendTime) {
        Long id = userUtil.getUser().getId();
        Letter letter = new Letter();
        letter.setContent(content);
        letter.setUserFromId(id);
        letter.setUserToId(userId);
        letter.setSendTime(sendTime);
        save(letter);

        return Response.builder().code(200).build();
    }

    @Override
    public Response deleteLetters(Long userId) {
        Long id = userUtil.getUser().getId();
        remove(new QueryWrapper<Letter>()
                .eq("user_from_id", userId)
                .eq("user_to_id", id)
                .or()
                .eq("user_from_id", id)
                .eq("user_to_id", userId));
        return Response.builder().code(200).build();
    }

    @Override
    public Letter saveLetter(LetterRequest letterRequest) {
        Letter letter = new Letter();
        letter.setContent(letterRequest.getContent());
        letter.setUserFromId(letterRequest.getUserFromId());
        letter.setUserToId(letterRequest.getUserToId());
        letter.setSendTime(DataUtil.formatStringToDate(letterRequest.getTime()));
        save(letter);
        return letter;
    }

    @Override
    public LetterOverviewVO getLetterOverviewVO(Letter letter) {
        LetterOverviewVO letterOverviewVO = new LetterOverviewVO();
        Long userFromId = letter.getUserFromId();
        Long userToId = letter.getUserToId();
        // 查询未读数量
        Long count = count(new QueryWrapper<Letter>()
                .eq("user_from_id", userFromId)
                .eq("user_to_id", userToId)
                .eq("is_read", 0));
        letterOverviewVO.setUnReadCount(Math.toIntExact(count));
        // 查询头像、姓名
        User user = userService.getById(userFromId);
        letterOverviewVO.setUserId(userFromId.toString());
        letterOverviewVO.setUserName(user.getUsername());
        letterOverviewVO.setUserAvatar(user.getAvatar());
        // 查询最新消息、最新内容
        List<Letter> list = list(new QueryWrapper<Letter>()
                .eq("user_from_id", userFromId)
                .eq("user_to_id", userToId)
                .or()
                .eq("user_to_id", userFromId)
                .eq("user_from_id", userToId)
                .orderByDesc("send_time"));
        letterOverviewVO.setNewContent(list.get(0).getContent());
        letterOverviewVO.setNewDate(DataUtil.formatDateTime(list.get(0).getSendTime()));
        // 查询关注、粉丝、是否屏蔽
        boolean isStar = followService.isFollowedByUser(userToId, userFromId);
        boolean isFan = followService.isFollowedByUser(userFromId, userToId);
        letterOverviewVO.setFan(isFan);
        letterOverviewVO.setStar(isStar);
        BlockedLetter one = blockedLetterService.getOne(new QueryWrapper<BlockedLetter>()
                .eq("user_id", userToId)
                .eq("blocked_user_id", user.getId()));
        letterOverviewVO.setBlock(one != null);

        return letterOverviewVO;
    }

    public LetterDetailVO convertToLetterDetailVO(Letter letter) {
        Long id = userUtil.getUser().getId();
        LetterDetailVO letterDetailVO = new LetterDetailVO();
        letterDetailVO.setSelf(Objects.equals(letter.getUserFromId(), id));
        letterDetailVO.setSendTime(DataUtil.formatDateTime(letter.getSendTime()));
        letterDetailVO.setContent(letter.getContent());
        return letterDetailVO;
    }
}
