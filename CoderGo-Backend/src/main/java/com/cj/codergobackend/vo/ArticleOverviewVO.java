package com.cj.codergobackend.vo;

import lombok.Data;

@Data
public class ArticleOverviewVO {
    private Long userId;
    private String userAvatar;

    private Long articleId;
    private String articleTitle;
    private String articleContent;
    private String articleCreatedAt;
    private boolean isTop;
    private boolean isGem;

    private Long totalViews;
    private Long totalLikes;
    private Long totalReplies;
}
