package com.example.studycirclebackend.vo;

import lombok.Data;

@Data
public class LetterDetailVO {
    private boolean isSelf;
    private String content;
    private String sendTime;
}