package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Conversation;
import com.cj.codergobackend.vo.ConversationVO;

import java.util.List;

public interface ConversationService extends IService<Conversation> {

    Response getConversationAll();


    Response addConversation();

    Response delConversation(Long conversationId);

    Response setConversationName(Long conversationId, String messageContent);

    ConversationVO getConversationVO(Conversation conversation);
    List<ConversationVO> getConversationVOList(Long userId);
    List<ConversationVO> getConversationVOList(List<Conversation> conversationList);


}
