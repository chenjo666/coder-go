package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Conversation;
import com.cj.studycirclebackend.vo.ConversationVO;

public interface ConversationService extends IService<Conversation> {

    Response getConversationAll();


    Response addConversation();

    Response delConversation(Long conversationId);

    Response setConversationName(Long conversationId, String messageContent);

    ConversationVO getConversationVO(Conversation conversation);

}
