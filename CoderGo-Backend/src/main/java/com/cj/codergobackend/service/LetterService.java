package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.vo.LetterDetailVO;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Letter;
import com.cj.codergobackend.vo.LetterOverviewVO;

import java.util.Date;
import java.util.List;

public interface LetterService extends IService<Letter> {

    Response getLetterList(String token);

    Response getLetterDetails(Long userId);

    Response createLetter(Long userId, String content, Date sendTime);

    Response deleteLetters(Long toUserId);


    Letter saveLetter(Long userFromId, Long userToId, String content, String time);

    LetterOverviewVO getLetterOverviewVO(Long fromUserId, Long toUserId);

    List<LetterDetailVO> getLetterDetails(Long fromUserId, Long toUserId);
}
