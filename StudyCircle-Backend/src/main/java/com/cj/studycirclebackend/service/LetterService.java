package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.vo.LetterDetailVO;
import com.cj.studycirclebackend.websocket.LetterRequest;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Letter;
import com.cj.studycirclebackend.vo.LetterOverviewVO;

import java.util.Date;
import java.util.List;

public interface LetterService extends IService<Letter> {

    Response getLetterList(String token);

    Response getLetterDetails(Long userId);

    Response createLetter(Long userId, String content, Date sendTime);

    Response deleteLetters(Long toUserId);


    Letter saveLetterRequest(LetterRequest letterRequest);

    LetterOverviewVO getLetterOverviewVO(Long fromUserId, Long toUserId);

    List<LetterDetailVO> getLetterDetails(Long fromUserId, Long toUserId);
}
