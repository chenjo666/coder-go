package com.cj.codergobackend.vo;

import lombok.Data;

@Data
public class LetterDetailVO {
    private boolean isSelf;
    private String content;
    private String createdAt;
}
