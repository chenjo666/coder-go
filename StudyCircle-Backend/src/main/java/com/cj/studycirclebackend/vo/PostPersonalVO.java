package com.cj.studycirclebackend.vo;

import lombok.Data;

/**
 * 个人中心界面的 POST_VO，包括发布、收藏
 */
@Data
public class PostPersonalVO {
    private Long postId;                // 帖子 id
    private String postTitle;           // 帖子标题
    private String postTime;            // 帖子发布时间
    private Long postViews;             // 帖子访问量
    private Long postLikes;             // 帖子点赞数量
}
