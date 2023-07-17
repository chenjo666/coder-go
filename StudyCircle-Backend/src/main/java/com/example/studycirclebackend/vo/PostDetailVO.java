package com.example.studycirclebackend.vo;

import lombok.Data;

import java.util.List;

@Data
public class PostDetailVO {
    // 作者
    private Long authorId;              // 作者 id
    private String authorName;          // 帖子作者名
    private String authorAvatar;        // 帖子作者头像
    // 帖子
    private Long postId;                // 帖子 id
    private String postTitle;           // 帖子标题
    private String postTime;            // 帖子发布时间
    private String postType;            // 帖子类型
    private String postContent;         // 帖子内容
    // 其它
    private Integer postVisits;         // 帖子访问量
    private Integer postLikes;          // 帖子点赞数量
    private Integer postFavorites;      // 帖子收藏数
    private Integer postReplies;        // 帖子评论数量
    private Integer commentReplies;     // 评论评论数量   两者之和为全部数量
    private boolean isLike;             // 是否赞过
    private boolean isFavorite;         // 是否收藏过
    private boolean isFollowAuthor;     // 是否关注过作者
    private boolean isTop;              // 是否置顶
    private boolean isGem;              // 是否加精
    // 标签
    private List<String> postTags;
    // 外层评论
    private List<CommentVO> parentCommentListVO;
}
