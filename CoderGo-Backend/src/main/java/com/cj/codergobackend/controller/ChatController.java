package com.cj.codergobackend.controller;

import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.service.ConversationService;
import com.cj.codergobackend.service.ConversationMessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private ConversationService conversationService;
    @Resource
    private ConversationMessageService conversationMessageService;
    // v1 - 查询全部对话
    @GetMapping("/v1/conversations")
    public Response getConversations(@CookieValue("token") String token) {
        return conversationService.getConversationAll();
    }
    // v1 - 添加一条对话
    @PostMapping("/v1/conversations")
    public Response createConversation() {
        return conversationService.addConversation();
    }
    // v1 - 删除一条对话
    @DeleteMapping("/v1/conversations/{conversationId}")
    public Response deleteConversation(@PathVariable("conversationId") Long conversationId) {
        return conversationService.delConversation(conversationId);
    }
    // v1 - 修改对话名称
    @PutMapping("/v1/conversations/{conversationId}")
    public Response updateConversation(@PathVariable("conversationId") Long conversationId, @RequestParam String newConversationName) {
        return conversationService.setConversationName(conversationId, newConversationName);
    }
    // v1 - 查询对话消息
    @GetMapping("/v1/conversations/{conversationId}/messages")
    public Response getConversationMessages(@PathVariable("conversationId") Long conversationId) {
        return conversationMessageService.getMessage(conversationId);
    }
    // v1 - 新增一条消息
    @PostMapping("/v1/conversations/{conversationId}/messages")
    public Response createMessage(@PathVariable("conversationId") Long conversationId, @RequestBody Map<String, Object> map) {
        Long messageTargetId = Long.parseLong((String)map.get("messageTargetId"));
        String question = (String) map.get("question");
        return conversationMessageService.createMessage(conversationId, messageTargetId, question);
    }
    // v1 - 更新消息内容
    @PutMapping("/v1/conversations/messages/{messageId}")
    public Response updateMessage(@PathVariable("messageId") Long messageId) {
        return conversationMessageService.updateMessage(messageId);
    }
    // v1 - 删除一条消息
    @DeleteMapping("/v1/conversations/messages/{messageId}")
    public Response deleteMessage(@PathVariable("messageId") Long messageId) {
        return conversationMessageService.deleteMessage(messageId);
    }
}