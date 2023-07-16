package com.example.studycirclebackend.vo;

import lombok.Data;

@Data
public class NoticeVO {
    private Long userId;
    private String userAvatar;
    private String userName;
    private Long noticeId;
    private Integer noticeType;
    private String noticeTime;
    private Long postId;
    private String postTitle;
    private boolean isRead;
}
