package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Conversation;
import com.cj.studycirclebackend.vo.ConversationVO;

import java.util.List;

public interface ConversationService extends IService<Conversation> {

    Response getConversationAll();


    Response addConversation();

    Response delConversation(Long conversationId);

    Response setConversationName(Long conversationId, String messageContent);

    /**
     * VO 对象
     */
    ConversationVO getConversationVO(Conversation conversation);
    List<ConversationVO> getConversationVOList(Long userId);
    List<ConversationVO> getConversationVOList(List<Conversation> conversationList);


}
