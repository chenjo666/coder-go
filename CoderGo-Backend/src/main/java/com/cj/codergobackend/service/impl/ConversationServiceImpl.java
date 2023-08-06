package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.dao.ConversationMapper;
import com.cj.codergobackend.service.ConversationMessageService;
import com.cj.codergobackend.vo.ConversationVO;
import com.cj.codergobackend.vo.ConversationMessageVO;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Conversation;
import com.cj.codergobackend.service.ConversationService;
import com.cj.codergobackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);
    @Resource
    private UserUtil userUtil;
    @Resource
    private ConversationMessageService conversationMessageService;

    @Override
    public Response getConversationAll() {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }

        // 查询当前用户的全部对话信息，倒序排序
        List<ConversationVO> conversationVOList = getConversationVOList(userUtil.getUser().getId());
        if (conversationVOList == null) {
            return Response.notContent();
        }

        // 第一个对话的消息记录
        Map<String, Object> data = new HashMap<>();
        List<ConversationMessageVO> messageListVO = conversationMessageService.getMessageVOList(conversationVOList.get(0).getConversationId());
        data.put("conversationListVO", conversationVOList);
        data.put("messageListVO", messageListVO);
        return Response.ok(data);
    }


    @Override
    public Response addConversation() {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        Conversation conversation = new Conversation();
        conversation.setUserId(userUtil.getUser().getId());
        conversation.setName("新对话");
        conversation.setCreatedAt(new Date());
        conversation.setIsDeleted(0);
        boolean res = save(conversation);

        return res ? Response.ok(getConversationVO(conversation)) : Response.internalServerError();
    }

    @Override
    public Response delConversation(Long conversationId) {
        Conversation conversation = getById(conversationId);
        conversation.setIsDeleted(1);
        boolean res = updateById(conversation);
        return res ? Response.ok() : Response.notContent();
    }

    @Override
    public Response setConversationName(Long conversationId, String newName) {
        Conversation conversation = getById(conversationId);
        conversation.setName(newName);
        boolean res = updateById(conversation);
        return res ? Response.ok() : Response.notContent();
    }


    @Override
    public List<ConversationVO> getConversationVOList(Long userId) {
        if (userId == null) { return null;}

        // 查询当前用户的全部对话信息，倒序排序
        List<Conversation> conversationList = list(
                new QueryWrapper<Conversation>()
                        .eq("user_id", userId)
                        .eq("is_deleted", 0)
                        .orderByDesc("created_at"));
        if (conversationList == null) {
            return null;
        }
        return getConversationVOList(conversationList);
    }

    @Override
    public List<ConversationVO> getConversationVOList(List<Conversation> conversationList) {
        List<ConversationVO> conversationVOList = new ArrayList<>(conversationList.size());
        for (Conversation conversation : conversationList) {
            conversationVOList.add(getConversationVO(conversation));
        }
        return conversationVOList;
    }


    @Override
    public ConversationVO getConversationVO(Conversation conversation) {
        ConversationVO conversationVO = new ConversationVO();
        conversationVO.setConversationId(conversation.getId());
        conversationVO.setConversationName(conversation.getName());
        return conversationVO;
    }
}
