package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 查询帖子详情
     * @param postId
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/{postId}")
    public Response getPostDetail(@PathVariable("postId") Long postId, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        return postService.getPostDetail(postId, currentPage, pageSize);
    }
    /**
     * 新增帖子
     */
    @PostMapping
    public Response createPost(@RequestBody Map<String, Object> map) {
        String postTitle = (String) map.get("postTitle");
        String postContent = (String) map.get("postContent");
        String postType = (String) map.get("postType");
        List<String> postTags = (List<String>) map.get("postTags");
        return postService.createPost(postTitle, postContent, postType, postTags);
    }
    /**
     * 修改帖子内容
     * @param postId
     * @param map
     * @return
     */
    @PutMapping("/{postId}")
    public Response updatePost(@PathVariable("postId") Long postId, @RequestBody Map<String, String> map) {
        String postContent = map.get("content");
        return postService.updatePost(postId, postContent);
    }
    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    public Response deletePost(@PathVariable("postId") Long postId) {
        return postService.deletePost(postId);
    }



    /**
     * 查询收藏的帖子列表
     */
    @GetMapping("/collections/{userId}")
    public Response getFavorites(@PathVariable("userId") Long userId) {
        return postService.getFavoritePosts(userId);
    }
    /**
     * 查询发布的帖子列表
     */
    @GetMapping("/publications/{userId}")
    public Response getPublications(@PathVariable("userId") Long userId) {
        return postService.getPublishPosts(userId);
    }


    /**
     * 查询帖子业务
     * @param postType
     * @param orderMode
     * @param keyWord
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/v1")
    public Response getPosts(@RequestParam String postType, @RequestParam String orderMode,
                             @RequestParam String keyWord, @RequestParam Integer currentPage,
                             @RequestParam Integer pageSize) {
        return postService.getPosts(postType, orderMode, keyWord, currentPage, pageSize);
    }
    /**
     * 新点赞帖子业务
     * @param postId
     * @return
     */
    @PostMapping("/v1/{postId}/likes")
    public Response createPostLike(@PathVariable("postId") Long postId) {
        return postService.likePost(postId);
    }

    /**
     * 新取消点赞帖子业务
     * @param postId
     * @return
     */
    @DeleteMapping("/v1/{postId}/likes")
    public Response deletePostLike(@PathVariable("postId") Long postId) {
        return postService.dislikePost(postId);
    }
    /**
     * 新收藏帖子业务
     * @param postId
     * @return
     */
    @PostMapping("/v1/{postId}/collects")
    public Response createPostCollect(@PathVariable("postId") Long postId) {
        return postService.collectPost(postId);
    }

    /**
     * 新取消收藏帖子业务
     * @param postId
     * @return
     */
    @DeleteMapping("/v1/{postId}/collects")
    public Response deletePostCollect(@PathVariable("postId") Long postId) {
        return postService.unCollectPost(postId);
    }
}
