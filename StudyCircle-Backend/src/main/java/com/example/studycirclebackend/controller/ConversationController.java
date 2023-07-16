package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.ConversationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    @Resource
    private ConversationService conversationService;

    /**
     * 查询全部对话
     * @return
     */
    @GetMapping
    public Response getConversationAll() {
        return conversationService.getConversationAll();
    }
    /**
     * 添加对话
     * @return
     */
    @PostMapping
    public Response addConversation() {
        return conversationService.addConversation();
    }
    /**
     * 删除对话
     * @param conversationId
     * @return
     */
    @DeleteMapping("/{conversation_id}")
    public Response deleteConversation(@PathVariable("conversation_id") Long conversationId) {
        return conversationService.delConversation(conversationId);
    }
    /**
     * 修改对话
     * @param conversationId
     * @param newConversationName
     * @return
     */
    @PutMapping("/{conversation_id}")
    public Response setConversationNewName(@PathVariable("conversation_id") Long conversationId, @RequestParam String newConversationName) {
        return conversationService.setConversationName(conversationId, newConversationName);
    }

}