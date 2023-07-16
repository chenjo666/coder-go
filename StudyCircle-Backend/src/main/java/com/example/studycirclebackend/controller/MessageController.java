package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.MessageService;
import com.example.studycirclebackend.vo.MessageVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversations")
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * 查询某个对话下的全部消息
     * @param conversationId
     * @return
     */
    @GetMapping("/{conversation_id}/messages")
    public Response getMessages(@PathVariable("conversation_id") Long conversationId) {
        List<MessageVO> messageVOList = messageService.getMessages(conversationId);
        return Response.builder().code(200).data(messageVOList).build();
    }
    /**
     * 创建某个消息
     * @param conversationId
     * @param map
     * @return
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
     * @param messageId
     * @return
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
     * @param messageId
     * @return
     */
    @DeleteMapping("/messages/{message_id}")
    public Response deleteMessage(@PathVariable("message_id") Long messageId) {
        boolean result = messageService.deleteMessage(messageId);
        return Response.builder().code(result ? 200 : -1).build();
    }
}
