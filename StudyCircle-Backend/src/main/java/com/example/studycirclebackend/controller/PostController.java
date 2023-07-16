package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.CommentObjectType;
import com.example.studycirclebackend.pojo.Comment;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.*;
import com.example.studycirclebackend.vo.PostOverviewVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Resource
    private PostService postService;
    @Resource
    private FavoriteService favoriteService;

    /**
     * 查询帖子列表
     * @param postType
     * @param orderMode
     * @param keyWord
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping
    public Response searchPost(@RequestParam String postType, @RequestParam String orderMode,
                               @RequestParam String keyWord, @RequestParam Integer currentPage,
                               @RequestParam Integer pageSize) {
        return postService.searchPosts(postType, orderMode, keyWord, pageSize, currentPage);
    }
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
     * 收藏帖子
     */
    @PostMapping("/favorites")
    public Response createFavorite(@RequestBody Map<String, String> args) {
        Long postId = Long.parseLong(args.get("postId"));
        return favoriteService.createFavorite(postId);
    }
    /**
     * 删除收藏的帖子
     */
    @DeleteMapping("/favorites/{postId}")
    public Response deleteFavorite(@PathVariable("postId") Long postId) {
        return favoriteService.deleteFavorite(postId);
    }
    /**
     * 查询收藏的帖子列表
     */
    @GetMapping("/favorites/{userId}")
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


    @GetMapping("/v1")
    public Response getPosts(@RequestParam String postType, @RequestParam String orderMode,
                             @RequestParam String keyWord, @RequestParam Integer currentPage,
                             @RequestParam Integer pageSize) {
        return postService.getPosts(postType, orderMode, keyWord, currentPage, pageSize);
    }

}
