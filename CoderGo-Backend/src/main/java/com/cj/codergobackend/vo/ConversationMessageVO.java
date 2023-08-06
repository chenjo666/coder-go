package com.cj.codergobackend.vo;

import lombok.Data;

@Data
public class ConversationMessageVO {
    private Long messageId;
    private Long messageTargetId;
    private String role;
    private String content;
    private String createdAt;
}
