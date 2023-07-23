package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dao.ConversationMapper;
import com.cj.studycirclebackend.pojo.Message;
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
    private MessageMapper messageMapper;

    @Override
    public Response getConversationAll() {
        User user = userUtil.getUser();
        if (user == null) {
            return Response.builder().code(-1).msg("请先登录").build();
        }
        Map<String, Object> data = new HashMap<>();
        List<ConversationVO> conversationListVO = new ArrayList<>();
        // 查询当前用户的全部对话信息，倒序排序
        List<Conversation> conversationList = list(
                new QueryWrapper<Conversation>()
                        .eq("user_id", user.getId())
                        .eq("is_deleted", 0)
                        .orderByDesc("create_time"));
        if (conversationList != null) {
            for (Conversation conversation : conversationList) {
                ConversationVO conversationVO = new ConversationVO();
                conversationVO.setConversationId(conversation.getId());
                conversationVO.setConversationName(conversation.getName());
                conversationListVO.add(conversationVO);
            }
            // 查询最新对话的全部消息
            List<MessageVO> messageListVO = new ArrayList<>();
            List<Message> messageList = messageMapper.selectList(
                    new QueryWrapper<Message>().
                            eq("conversation_id", conversationList.get(0).getId())
                            .eq("is_deleted", 0)
                            .orderByAsc("send_time"));
            if (messageList != null){
                for (Message conversationMessage : messageList) {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setMessageId(conversationMessage.getId());
                    messageVO.setRole(conversationMessage.getRole());
                    messageVO.setContent(conversationMessage.getContent());
                    messageVO.setSendTime(DataUtil.formatDateTime(conversationMessage.getSendTime()));
                    messageListVO.add(messageVO);
                }
            }
            data.put("conversationListVO", conversationListVO);
            data.put("messageListVO", messageListVO);
        }

        return Response.builder().code(200).msg("").data(data).build();
    }


    @Override
    public Response addConversation() {
        User user = userUtil.getUser();
        if (user == null) {
            return Response.builder().code(-1).msg("请先登录！").build();
        }
        Conversation conversation = new Conversation();
        conversation.setUserId(user.getId());
        conversation.setName("新对话");
        conversation.setCreateTime(new Date());
        conversation.setIsDeleted(0);
        boolean res = save(conversation);
        if (res) {
            return Response.builder().code(-1).msg("服务器繁忙，请等待！").build();
        }
        ConversationVO conversationVO = new ConversationVO();
        conversationVO.setConversationId(conversation.getId());
        conversationVO.setConversationName(conversation.getName());

        return Response.builder().code(200).data(conversationVO).build();
    }

    @Override
    public Response delConversation(Long conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        Conversation conversation = getById(conversationId);
        if (conversation == null) {
            return Response.builder().code(-1).msg("该对话不存在！").build();
        }
        conversation.setIsDeleted(1);
        updateById(conversation);
        return Response.builder().code(200).msg("删除成功").build();
    }

    @Override
    public Response setConversationName(Long conversationId, String newName) {
        if (conversationId == null || newName == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        Conversation conversation = getById(conversationId);
        if (conversation == null) {
            return Response.builder().code(-1).msg("出现未知错误").build();
        }
        conversation.setName(newName);
        updateById(conversation);
        return Response.builder().code(200).msg("删除成功").build();
    }
}
