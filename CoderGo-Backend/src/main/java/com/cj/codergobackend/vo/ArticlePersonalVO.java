package com.cj.codergobackend.vo;

import lombok.Data;

/**
 * 个人中心界面的 POST_VO，包括发布、收藏
 */
@Data
public class ArticlePersonalVO {
    private Long articleId;                // 文章 id
    private String articleTitle;           // 文章标题
    private String articleCreatedAt;       // 文章发布时间
    private Long totalViews;             // 文章访问量
    private Long totalLikes;             // 文章点赞数量
}
