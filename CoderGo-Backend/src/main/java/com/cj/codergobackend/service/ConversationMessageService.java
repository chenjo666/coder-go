package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.pojo.ConversationMessage;
import com.cj.codergobackend.vo.ConversationMessageVO;
import com.cj.codergobackend.dto.Response;

import java.util.List;


public interface ConversationMessageService extends IService<ConversationMessage> {

    Response getMessage(Long conversationId);
    Response createMessage(Long conversationId, Long messageTargetId, String question);
    Response deleteMessage(Long messageId);
    Response updateMessage(Long messageId);


    ConversationMessageVO getMessageVO(ConversationMessage conversationMessage);

    List<ConversationMessageVO> getMessageVOList(Long conversationId);
    List<ConversationMessageVO> getMessageVOList(List<ConversationMessage> conversationMessageList);
}
