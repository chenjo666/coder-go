package com.cj.studycirclebackend.service;

import com.cj.studycirclebackend.vo.MessageVO;
import com.cj.studycirclebackend.dto.Response;

import java.util.List;


public interface MessageService {

    List<MessageVO> createMessage(Long conversationId, Long messageTargetId, String question);
    Response deleteMessage(Long messageId);
    MessageVO updateMessage(Long messageId);
    List<MessageVO> getMessages(Long conversationId);
}
