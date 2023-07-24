package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dao.ConversationMapper;
import com.cj.studycirclebackend.pojo.Message;
import com.cj.studycirclebackend.service.MessageService;
import com.cj.studycirclebackend.vo.ConversationVO;
import com.cj.studycirclebackend.vo.MessageVO;
import com.cj.studycirclebackend.dao.MessageMapper;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Conversation;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.ConversationService;
import com.cj.studycirclebackend.util.DataUtil;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
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
    private MessageService messageService;

    @Override
    public Response getConversationAll() {
        User user = userUtil.getUser();
        if (user == null) {
            return Response.invalidOperation();
        }
        Map<String, Object> data = new HashMap<>();
        // 查询当前用户的全部对话信息，倒序排序
        List<ConversationVO> conversationVOList = getConversationVOList(user.getId());

        // 第一个对话的消息记录
        if (conversationVOList != null) {
            List<MessageVO> messageListVO = messageService.getMessageVOList(conversationVOList.get(0).getConversationId());
            data.put("conversationListVO", conversationVOList);
            data.put("messageListVO", messageListVO);
        }

        return Response.ok(data);
    }


    @Override
    public Response addConversation() {
        User user = userUtil.getUser();
        if (user == null) {
            return Response.invalidOperation();
        }
        Conversation conversation = new Conversation();
        conversation.setUserId(user.getId());
        conversation.setName("新对话");
        conversation.setCreateTime(new Date());
        conversation.setIsDeleted(0);
        save(conversation);

        return Response.ok(getConversationVO(conversation));
    }

    @Override
    public Response delConversation(Long conversationId) {
        if (conversationId == null) {
            return Response.badRequest();
        }
        Conversation conversation = getById(conversationId);
        conversation.setIsDeleted(1);
        updateById(conversation);
        return Response.ok();
    }

    @Override
    public Response setConversationName(Long conversationId, String newName) {
        if (conversationId == null || StringUtils.isBlank(newName)) {
            return Response.badRequest();
        }
        Conversation conversation = getById(conversationId);
        conversation.setName(newName);
        updateById(conversation);
        return Response.ok();
    }


    @Override
    public List<ConversationVO> getConversationVOList(Long userId) {
        if (userId == null) return null;
        // 查询当前用户的全部对话信息，倒序排序
        List<Conversation> conversationList = list(
                new QueryWrapper<Conversation>()
                        .eq("user_id", userId)
                        .eq("is_deleted", 0)
                        .orderByDesc("create_time"));
        return getConversationVOList(conversationList);
    }

    @Override
    public List<ConversationVO> getConversationVOList(List<Conversation> conversationList) {
        if (conversationList == null) return null;

        List<ConversationVO> conversationVOList = new ArrayList<>(conversationList.size());
        for (Conversation conversation : conversationList) {
            conversationVOList.add(getConversationVO(conversation));
        }
        return conversationVOList;
    }


    @Override
    public ConversationVO getConversationVO(Conversation conversation) {
        if (conversation == null) return null;
        ConversationVO conversationVO = new ConversationVO();
        conversationVO.setConversationId(conversation.getId());
        conversationVO.setConversationName(conversation.getName());
        return conversationVO;
    }
}
