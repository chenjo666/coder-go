package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.ConversationService;
import com.example.studycirclebackend.service.MessageService;
import com.example.studycirclebackend.vo.MessageVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    @Resource
    private ConversationService conversationService;
    @Resource
    private MessageService messageService;
    /**
     * 查询全部对话
     */
    @GetMapping
    public Response getConversationAll() {
        return conversationService.getConversationAll();
    }
    /**
     * 添加对话
     */
    @PostMapping
    public Response addConversation() {
        return conversationService.addConversation();
    }
    /**
     * 删除对话
     */
    @DeleteMapping("/{conversation_id}")
    public Response deleteConversation(@PathVariable("conversation_id") Long conversationId) {
        return conversationService.delConversation(conversationId);
    }
    /**
     * 修改对话
     */
    @PutMapping("/{conversation_id}")
    public Response setConversationNewName(@PathVariable("conversation_id") Long conversationId, @RequestParam String newConversationName) {
        return conversationService.setConversationName(conversationId, newConversationName);
    }
    /**
     * 查询某个对话下的全部消息
     */
    @GetMapping("/{conversation_id}/messages")
    public Response getMessages(@PathVariable("conversation_id") Long conversationId) {
        List<MessageVO> messageVOList = messageService.getMessages(conversationId);
        return Response.builder().code(200).data(messageVOList).build();
    }
    /**
     * 创建某个消息
     */
    @PostMapping("/{conversation_id}/messages")
    public Response createMessage(@PathVariable("conversation_id") Long conversationId, @RequestBody Map<String, Object> map) {
        Long messageTargetId = Long.parseLong((String)map.get("messageTargetId"));
        String question = (String) map.get("question");
        List<MessageVO> messageVOList = messageService.createMessage(conversationId, messageTargetId, question);
        if (messageVOList == null) {
            return Response.builder().code(-1).build();
        }
        return Response.builder().code(200).data(messageVOList).build();
    }
    /**
     * 更新某个消息
     */
    @PutMapping("/messages/{message_id}")
    public Response updateMessage(@PathVariable("message_id") Long messageId) {
        MessageVO messageVO = messageService.updateMessage(messageId);
        if (messageVO == null) {
            return Response.builder().code(-1).build();
        }
        return Response.builder().code(200).data(messageVO).build();
    }
    /**
     * 删除某个消息
     */
    @DeleteMapping("/messages/{message_id}")
    public Response deleteMessage(@PathVariable("message_id") Long messageId) {
        return messageService.deleteMessage(messageId);
    }
}