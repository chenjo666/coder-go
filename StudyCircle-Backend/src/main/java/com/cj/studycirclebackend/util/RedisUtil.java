package com.cj.studycirclebackend.util;

public class RedisUtil {
    private static final String SPLIT = ":";
    private static final String USER = "user";
    private static final String TICKET = "ticket";
    private static final String POST = "post";
    private static final String COMMENT = "comment";
    private static final String LIKE = "like";
    private static final String FAVORITE = "favorite";
    private static final String FOLLOWING = "following";
    private static final String FOLLOWER = "follower";
    private static final String VIEW = "view";
    public static String getTicketKey(String token) {
        return TICKET + SPLIT + token;
    }


    /**
     * 点赞帖子的键
     */
    public static String getPostLikeKey(Long postId) {
        return POST + SPLIT + postId + SPLIT + LIKE;
    }
    /**
     * 点赞评论的键
     */
    public static String getCommentLikeKey(Long commentId) {
        return COMMENT + SPLIT + commentId + SPLIT + LIKE;
    }
    /**
     * 帖子的收藏列表键
     */
    public static String getPostFavoriteKey(Long postId) {
        return POST + SPLIT + postId + SPLIT + FAVORITE;
    }
    /**
     * 用户收藏的帖子列表
     */
    public static String getUserFavoriteKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + FAVORITE;
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
     * 帖子的访问量的键
     */
    public static String getPostViewKey(Long postId) {
        return POST + SPLIT + postId + SPLIT + VIEW;
    }
}
