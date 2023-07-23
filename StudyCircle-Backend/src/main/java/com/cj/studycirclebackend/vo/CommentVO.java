package com.cj.studycirclebackend.vo;

import lombok.Data;

import java.util.List;
@Data
public class CommentVO {
    // 作者
    private Long userId;
    private String userName;      // 评论作者名
    private String userAvatar;    // 评论作者头像
    // 评论
    private Long commentId;         // 评论id
    private String commentTime;     // 评论发布时间
    private String commentContent;  // 评论内容
    private Integer commentReplies; // 评论回复数量
    private Double commentScore;   // 评论分数
    // 点赞
    private Integer commentLikes;   // 评论点赞数量
    private boolean isLike;         // 评论是否被当前用户点赞过
    // 子评论
    private List<CommentVO> childCommentListVO;
}
