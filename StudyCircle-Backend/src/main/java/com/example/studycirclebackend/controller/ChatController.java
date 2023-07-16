package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.dto.ai.AIMessage;
import com.example.studycirclebackend.enums.ResponseCode;
import com.example.studycirclebackend.enums.ResponseMsg;
import com.example.studycirclebackend.util.AIChatUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {
    @Resource
    private AIChatUtil aiChatUtil;

    @RequestMapping(path = "/chat/question",method = RequestMethod.GET)
    public Response openAiChat(@RequestParam("question")String question){
        if (StringUtils.isBlank(question)) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.NULL_OBJECT.getValue())
                    .build();
        }
//        return Response.builder().code(200).data(aiChatUtil.chatWithClientProxy(question)).build();
        List<AIMessage> messages = new ArrayList<>();
        messages.add(new AIMessage("user", question));
        return Response.builder()
                .code(ResponseCode.SUCCESS.getValue())
                .data(aiChatUtil.createChatCompletion(messages))
                .build();
    }
}
