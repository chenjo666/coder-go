package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.Message;
import com.cj.studycirclebackend.vo.MessageVO;
import com.cj.studycirclebackend.dto.Response;

import java.util.List;


public interface MessageService extends IService<Message> {

    List<MessageVO> createMessage(Long conversationId, Long messageTargetId, String question);
    Response deleteMessage(Long messageId);
    MessageVO updateMessage(Long messageId);


    MessageVO getMessageVO(Message message);
    List<MessageVO> getMessageVOList(Long conversationId);
    List<MessageVO> getMessageVOList(List<Message> messageList);
}
