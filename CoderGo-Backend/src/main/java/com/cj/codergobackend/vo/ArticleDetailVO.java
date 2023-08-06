package com.cj.codergobackend.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleDetailVO {
    // 作者
    private Long authorId;              // 作者 id
    private String authorName;          // 文章作者名
    private String authorAvatar;        // 文章作者头像
    // 文章
    private Long articleId;             // 文章 id
    private String articleTitle;        // 文章标题
    private String articleCreatedAt;    // 文章发布时间
    private String articleType;         // 文章类型
    private String articleContent;      // 文章内容
    // 其它
    private Integer totalViews;         // 文章访问量
    private Integer totalLikes;         // 文章点赞数量
    private Integer totalFavorites;     // 文章收藏数
    private Integer totalReplies;       // 文章回复数量
    private Integer totalComments;      // 文章评论数量
    private boolean isLike;             // 是否赞过
    private boolean isFavorite;         // 是否收藏过
    private boolean isFollowAuthor;     // 是否关注过作者
    private boolean isTop;              // 是否置顶
    private boolean isGem;              // 是否加精
    // 标签
    private List<String> articleTags;
    // 外层评论
    private List<ArticleCommentVO> parentCommentListVO;
}
