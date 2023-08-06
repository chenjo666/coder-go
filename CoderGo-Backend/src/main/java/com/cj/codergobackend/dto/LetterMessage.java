package com.cj.codergobackend.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LetterMessage {
    // 消息发送者
    private String from;
    // 消息接收者
    private String to;
    // 消息类型
    private int type;
    // 消息数据域
    private Object data;

}
