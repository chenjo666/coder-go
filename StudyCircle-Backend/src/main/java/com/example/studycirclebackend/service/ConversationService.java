package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Conversation;

public interface ConversationService extends IService<Conversation> {

    Response getConversationAll();


    Response addConversation();

    Response delConversation(Long conversationId);

    Response setConversationName(Long conversationId, String messageContent);

}
