package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.vo.PostPersonalVO;
import com.example.studycirclebackend.vo.PostOverviewVO;
import com.example.studycirclebackend.vo.PostDetailVO;

import java.util.List;

public interface PostService extends IService<Post> {

    /********************************** 四个 CRUD 方法 **********************************/
    Response getPostDetail(Long postId, Integer currentPage, Integer pageSize);
    Response createPost(String postTitle, String postContent, String postType, List<String> postTags);
    Response updatePost(Long postId, String newContent);
    Response deletePost(Long postId);


    /********************************** 两个查询帖子方法 **********************************/
    // 得到某人发布的帖子列表
    Response getPostPublications(Long userId);
    // 得到某人收藏的帖子列表
    Response getPostFavorites(Long userId);


    /********************************** 两个搜索帖子方法 **********************************/
    /**
     * Mysql 搜索帖子，进行模糊查询
     */
    // 参数：帖子类型，排序规则，关键字，页码，每页限制
    Response searchPostsByMySQL(String type, String order, String key, Integer page, Integer limit);
    /**
     * Elasticsearch 搜索帖子，进行结果关键词的高亮显示
     * @param type
     * @param order
     * @param key
     * @param page
     * @param limit
     * @return
     */
    Response searchPostsByElasticsearch(String type, String order, String key, Integer page, Integer limit);


    /********************************** 三个转换对象方法 **********************************/
    PostOverviewVO getPostOverviewVO(Post post);
    PostDetailVO getPostDetailVO(Post post);
    PostPersonalVO getPostPersonVO(Post post);


    /********************************** 四个点赞、收藏相关方法 **********************************/
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

    /*************************************** 关键词提示 ****************************/
    Response getSearchSuggestions(String key);
}
