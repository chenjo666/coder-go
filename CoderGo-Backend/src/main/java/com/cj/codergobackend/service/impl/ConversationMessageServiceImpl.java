package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.pojo.ConversationMessage;
import com.cj.codergobackend.vo.ConversationMessageVO;
import com.cj.codergobackend.dao.ConversationMessageMapper;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.dto.ai.AIMessage;
import com.cj.codergobackend.dto.ai.AIResponse;
import com.cj.codergobackend.enums.MessageRole;
import com.cj.codergobackend.service.ConversationMessageService;
import com.cj.codergobackend.util.AIChatUtil;
import com.cj.codergobackend.util.DataUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConversationMessageServiceImpl extends ServiceImpl<ConversationMessageMapper, ConversationMessage> implements ConversationMessageService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationMessageServiceImpl.class);
    @Resource
    private AIChatUtil aiChatUtil;

    @Override
    public Response createMessage(Long conversationId, Long messageTargetId, String question) {
        List<AIMessage> messages = new ArrayList<>();
        // 短文本历史记录

        messages.add(new AIMessage(MessageRole.USER.getValue(), question));
        AIResponse aiResponse = aiChatUtil.createChatCompletion(messages);
        String content = aiResponse.getChoices().get(0).getMessage().getContent();
        long promptTokens = aiResponse.getUsage().getPromptTokens();
        long completionTokens = aiResponse.getUsage().getCompletionTokens();

        List<ConversationMessageVO> conversationMessageVOList = new ArrayList<>();
        // 自身对话
        ConversationMessage userConversationMessage = new ConversationMessage();
        userConversationMessage.setConversationId(conversationId);
        userConversationMessage.setContent(question);
        userConversationMessage.setMessageTargetId(messageTargetId);
        userConversationMessage.setCreatedAt(new Date());
        userConversationMessage.setRole(MessageRole.USER.getValue());
        userConversationMessage.setIsDeleted(0);
        userConversationMessage.setTokens(promptTokens);
        save(userConversationMessage);
        ConversationMessageVO userConversationMessageVO = getMessageVO(userConversationMessage);

        // 对方对话
        ConversationMessage aiConversationMessage = new ConversationMessage();
        aiConversationMessage.setConversationId(conversationId);
        aiConversationMessage.setContent(content);
        aiConversationMessage.setMessageTargetId(userConversationMessage.getId()); // 设置对话 id
        aiConversationMessage.setCreatedAt(DataUtil.afterRandomSeconds(userConversationMessage.getCreatedAt()));
        aiConversationMessage.setRole(MessageRole.ASSISTANT.getValue());
        aiConversationMessage.setIsDeleted(0);
        aiConversationMessage.setTokens(completionTokens);
        save(aiConversationMessage);
        ConversationMessageVO aiConversationMessageVO = getMessageVO(aiConversationMessage);

        conversationMessageVOList.add(userConversationMessageVO);
        conversationMessageVOList.add(aiConversationMessageVO);
        return Response.ok(conversationMessageVOList);
    }

    @Override
    public ConversationMessageVO getMessageVO(ConversationMessage conversationMessage) {
        ConversationMessageVO conversationMessageVO = new ConversationMessageVO();
        conversationMessageVO.setMessageId(conversationMessage.getId());
        conversationMessageVO.setContent(conversationMessage.getContent());
        conversationMessageVO.setCreatedAt(DataUtil.formatDateTime(conversationMessage.getCreatedAt()));
        conversationMessageVO.setRole(conversationMessage.getRole());
        return conversationMessageVO;
    }



    @Override
    public Response deleteMessage(Long messageId) {
        boolean res = update(new UpdateWrapper<ConversationMessage>().set("is_deleted", 1).eq("id", messageId));
        return res ? Response.ok() : Response.notContent();
    }

    @Override
    public Response updateMessage(Long messageId) {
        List<AIMessage> messages = new ArrayList<>();
        ConversationMessage conversationMessage = getById(messageId);
        if (conversationMessage == null) {
            return Response.notContent();
        }
        String question = getById(conversationMessage.getMessageTargetId()).getContent();
        // 历史会话
        messages.add(new AIMessage("user", question));
        AIResponse aiResponse = aiChatUtil.createChatCompletion(messages);
        String content = aiResponse.getChoices().get(0).getMessage().getContent();
        long tokens = aiResponse.getUsage().getCompletionTokens();
        conversationMessage.setContent(content);
        conversationMessage.setTokens(tokens);
        boolean res = saveOrUpdate(conversationMessage);

        return res ? Response.ok(getMessageVO(conversationMessage)) : Response.internalServerError();
    }

    @Override
    public Response getMessage(Long conversationId) {
        List<ConversationMessageVO> res = getMessageVOList(conversationId);
        return res != null ? Response.ok(res) : Response.notContent();
    }

    @Override
    public List<ConversationMessageVO> getMessageVOList(Long conversationId) {
        List<ConversationMessage> conversationMessages = list(new QueryWrapper<ConversationMessage>().
                eq("conversation_id", conversationId)
                .eq("is_deleted", 0)
                .orderByAsc("created_at"));
        if (conversationMessages == null) {
            return null;
        }
        return getMessageVOList(conversationMessages);
    }
    @Override
    public List<ConversationMessageVO> getMessageVOList(List<ConversationMessage> conversationMessageList) {
        List<ConversationMessageVO> conversationMessageVOList = new ArrayList<>(conversationMessageList.size());
        for (ConversationMessage conversationMessage : conversationMessageList) {
            conversationMessageVOList.add(getMessageVO(conversationMessage));
        }
        return conversationMessageVOList;
    }
}
