package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Letter;
import com.example.studycirclebackend.server.LetterRequest;
import com.example.studycirclebackend.vo.LetterOverviewVO;

import java.util.Date;

public interface LetterService extends IService<Letter> {

    Response getLetterList(String token);

    Response getLetterDetails(Long userId);

    Response createLetter(Long userId, String content, Date sendTime);

    Response deleteLetters(Long userId);


    Letter saveLetter(LetterRequest letterRequest);

    LetterOverviewVO getLetterOverviewVO(Letter letter);
}
