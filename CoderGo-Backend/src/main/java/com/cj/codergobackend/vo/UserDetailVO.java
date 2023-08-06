package com.cj.codergobackend.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDetailVO {
    private Long userId;
    private String username;
    private String userAvatar;

    private Long totalFollowings;
    private Long totalFollowers;
    private Long totalViews;
    private Long totalLikes;
    private Long totalFavorites;

    private List<ArticlePersonalVO> articles;
}
