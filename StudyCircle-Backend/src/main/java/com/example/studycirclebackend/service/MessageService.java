package com.example.studycirclebackend.service;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.vo.MessageVO;

import java.util.List;


public interface MessageService {

    List<MessageVO> createMessage(Long conversationId, Long messageTargetId, String question);
    Response deleteMessage(Long messageId);
    MessageVO updateMessage(Long messageId);
    List<MessageVO> getMessages(Long conversationId);
}
