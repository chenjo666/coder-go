package com.example.studycirclebackend.util;

public class RedisUtil {
    private static final String SPLIT = ":";
    private static final String USER = "user";
    private static final String TICKET = "ticket";
    private static final String POST = "post";
    private static final String COMMENT = "comment";
    private static final String LIKE = "like";
    private static final String COLLECT = "collect";
    private static final String FOLLOWING = "following";
    private static final String FOLLOWER = "follower";
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
     * 收藏帖子的键
     */
    public static String getPostCollectKey(Long postId) {
        return POST + SPLIT + postId + SPLIT + COLLECT;
    }
    /**
     * 用户的关注列表的键
     */
    public static String getUserFOLLOWINGKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + FOLLOWING;
    }
    /**
     * 用户的粉丝列表的键
     */
    public static String getUserFollowerKey(Long userId) {
        return USER + SPLIT + userId + SPLIT + FOLLOWER;
    }
}
