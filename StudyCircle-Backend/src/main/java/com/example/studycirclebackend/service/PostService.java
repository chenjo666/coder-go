package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.vo.PostPersonalVO;
import com.example.studycirclebackend.vo.PostOverviewVO;
import com.example.studycirclebackend.vo.PostDetailVO;

import java.util.List;

public interface PostService extends IService<Post> {

    Response getPostDetail(Long postId, Integer currentPage, Integer pageSize);

    Response createPost(String postTitle, String postContent, String postType, List<String> postTags);
    Response updatePost(Long postId, String newContent);
    Response deletePost(Long postId);

    PostDetailVO getPostDetailVO(Post post);

    PostPersonalVO convertToPersonPostVO(Post post);


    // 得到某人发布的帖子列表
    Response getPublishPosts(Long userId);

    // 得到某人收藏的帖子列表
    Response getFavoritePosts(Long userId);


    /**
     * 得到帖子的大纲
     */
    // 参数：帖子类型，排序规则，关键字，页码，每页限制
    Response getPosts(String type, String order, String key, Integer page, Integer limit);
    // 得到帖子的总数
    Integer getPostTotal(String type, String order, String key);
    // 转换对象
    PostOverviewVO getPostOverviewVO(Post post);


    /**
     * 帖子的点赞业务
     */
    Response likePost(Long postId);

    /**
     * 帖子的取消点赞业务
     */
    Response dislikePost(Long postId);

    /**
     * 帖子的收藏业务
     */
    Response favoritePost(Long postId);
    /**
     * 帖子的取消收藏业务
     */
    Response unFavoritePost(Long postId);

}
