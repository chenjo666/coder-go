package com.cj.codergobackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LetterResponse {
    // 对方信息
    private String userId;
    private String userName;
    private String userAvatar;
    // 是否粉丝、关注、屏蔽
    private boolean isStar;
    private boolean isFan;
    private boolean isBlock;
    // 最新内容、时间
    private String newContent;
    private String newDate;
    // 未读数量
    private Integer totalUnread;
}
