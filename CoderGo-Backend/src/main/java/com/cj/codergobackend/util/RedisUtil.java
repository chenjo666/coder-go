package com.cj.codergobackend.util;

public class RedisUtil {
    private static final String SPLIT = ":";
    private static final String USER = "user";
    private static final String TICKET = "ticket";
    private static final String ARTICLE = "article";
    private static final String ARTICLE_COMMENT = "comment";
    private static final String LIKE = "like";
    private static final String ARTICLE_FAVORITE = "favorite";
    private static final String FOLLOWING = "following";
    private static final String FOLLOWER = "follower";
    private static final String ARTICLE_VIEW = "view";
    public static String getTicketKey(String token) {
        return TICKET + SPLIT + token;
    }


    /**
     * 点赞文章的键
     */
    public static String getArticleLikeKey(Long articleId) {
        return ARTICLE + SPLIT + articleId + SPLIT + LIKE;
    }
    /**
     * 点赞评论的键
     */
    public static String getArticleCommentLikeKey(Long commentId) {
        return ARTICLE_COMMENT + SPLIT + commentId + SPLIT + LIKE;
    }
    /**
     * 文章的收藏列表键
     */
    public static String getArticleFavoriteKey(Long articleId) {
        return ARTICLE + SPLIT + articleId + SPLIT + ARTICLE_FAVORITE;
    }
    /**
     * 用户收藏的文章列表
     */
    public static String getUserFavoriteKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + ARTICLE_FAVORITE;
    }
    /**
     * 用户的关注列表的键
     */
    public static String getUserFollowingKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + FOLLOWING;
    }
    /**
     * 用户的粉丝列表的键
     */
    public static String getUserFollowerKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + FOLLOWER;
    }
    /**
     * 文章的访问量的键
     */
    public static String getArticleViewKey(Long articleId) {
        return ARTICLE + SPLIT + articleId + SPLIT + ARTICLE_VIEW;
    }
}
