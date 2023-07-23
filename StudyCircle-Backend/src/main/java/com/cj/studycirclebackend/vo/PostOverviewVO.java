package com.cj.studycirclebackend.vo;

import lombok.Data;

@Data
public class PostOverviewVO {
    private Long userId;
    private String userAvatar;

    private Long postId;
    private String postTitle;
    private String postContent;
    private String postTime;
    private boolean isTop;
    private boolean isGem;

    private Long postViews;
    private Long postLikes;
    private Long postReplies;
}
