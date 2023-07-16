package com.example.studycirclebackend.vo;

import lombok.Data;

@Data
public class MessageVO {
    private Long messageId;
    private Long messageTargetId;
    private String role;
    private String content;
    private String sendTime;
}
