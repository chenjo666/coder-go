package com.cj.codergobackend.vo;

import lombok.Data;

@Data
public class NoticeVO {
    private Long userId;
    private String userAvatar;
    private String userName;
    private Long noticeId;
    private Integer noticeType;
    private String noticeCreatedAt;
    private Long articleId;
    private String articleTitle;
    private boolean isRead;
}
