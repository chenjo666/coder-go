package com.cj.codergobackend.vo;

import lombok.Data;

import java.util.List;
@Data
public class ArticleCommentVO {
    // 作者
    private Long userId;
    private String userName;            // 评论作者名
    private String userAvatar;          // 评论作者头像
    // 评论
    private Long commentId;             // 评论id
    private String commentCreatedAt;    // 评论发布时间
    private String commentContent;      // 评论内容
    // 点赞
    private Integer totalReplies;       // 评论回复数量
    private Integer totalLikes;         // 评论点赞数量
    private boolean isLike;             // 评论是否被当前用户点赞过
    // 子评论
    private List<ArticleCommentVO> childCommentListVO;
}
