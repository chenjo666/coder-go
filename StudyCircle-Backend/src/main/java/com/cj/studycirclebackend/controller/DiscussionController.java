package com.cj.studycirclebackend.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.service.CommentService;
import com.cj.studycirclebackend.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 讨论模块
 */

@RestController
@RequestMapping("/discussion")
public class DiscussionController {
    @Resource
    private PostService postService;
    @Resource
    private CommentService commentService;

    // v1 - 查询帖子详情
    @GetMapping("/v1/posts/{postId}")
    public Response queryPost(@PathVariable("postId") Long postId, @RequestParam Integer commentPage, @RequestParam Integer commentLimit) {
        return postService.getPostDetail(postId, commentPage, commentLimit);
    }
    // v1 - 查询帖子列表(mysql)
    @GetMapping("/v1/posts")
    public Response searchPostsByMysql(@RequestParam String postType, @RequestParam String postSort,
                             @RequestParam String postKey, @RequestParam Integer postPage,
                             @RequestParam Integer postLimit) {
        return postService.searchPostsByMySQL(postType, postSort, postKey, postPage, postLimit);
    }
    // v2 - 查询帖子列表(elasticsearch)
    @GetMapping("/v2/posts")
    public Response searchPostsByElasticsearch(@RequestParam String postType, @RequestParam String postSort,
                                @RequestParam String postKey, @RequestParam Integer postPage,
                                @RequestParam Integer postLimit) {
        return postService.searchPostsByElasticsearch(postType, postSort, postKey, postPage, postLimit);
    }
    // v1 - 新建帖子
    @PostMapping("/v1/posts")
    public Response createPost(@RequestBody Map<String, Object> map) {
        String postTitle = (String) map.get("postTitle");
        String postContent = (String) map.get("postContent");
        String postType = (String) map.get("postType");
        List<String> postTags = (List<String>) map.get("postTags");
        return postService.createPost(postTitle, postContent, postType, postTags);
    }
    // v1 - 修改帖子内容
    @PutMapping("/v1/posts/{postId}")
    public Response updatePost(@PathVariable("postId") Long postId, @RequestBody Map<String, String> map) {
        String postContent = map.get("content");
        return postService.updatePost(postId, postContent);
    }
    // v1 - 删除帖子
    @DeleteMapping("/v1/posts/{postId}")
    public Response deletePost(@PathVariable("postId") Long postId) {
        return postService.deletePost(postId);
    }
    // v1 - 查询收藏的帖子列表
    @GetMapping("/v1/posts/favorites/{userId}")
    public Response getPostFavorites(@PathVariable("userId") Long userId) {
        return postService.getPostFavorites(userId);
    }
    // v1 - 查询发布的帖子列表
    @GetMapping("/v1/posts/publications/{userId}")
    public Response getPostPublications(@PathVariable("userId") Long userId) {
        return postService.getPostPublications(userId);
    }
    // v1 - 点赞帖子业务
    @PostMapping("/v1/posts/{postId}/likes")
    public Response createPostLike(@PathVariable("postId") Long postId) {
        return postService.likePost(postId);
    }
    // v1 - 取消点赞帖子业务
    @DeleteMapping("/v1/posts/{postId}/likes")
    public Response deletePostLike(@PathVariable("postId") Long postId) {
        return postService.dislikePost(postId);
    }
    // v1 - 收藏帖子业务
    @PostMapping("/v1/posts/{postId}/favorites")
    public Response createPostFavorite(@PathVariable("postId") Long postId) {
        return postService.favoritePost(postId);
    }
    // v1 - 取消收藏帖子业务
    @DeleteMapping("/v1/posts/{postId}/favorites")
    public Response deletePostFavorite(@PathVariable("postId") Long postId) {
        return postService.unFavoritePost(postId);
    }

    // v1 - 根据帖子查询评论
    @GetMapping("/v1/posts/{postId}/comments")
    public Response getCommentsByPost(@PathVariable("postId") Long postId, @RequestParam String commentSort,
                                      @RequestParam Integer commentPage, @RequestParam Integer commentLimit) {
        return commentService.getAllCommentsByPost(postId, commentSort, commentPage, commentLimit);
    }
    // v1 - 根据评论查询评论
    @GetMapping("/v1/posts/comments/{commentId}/comments")
    public Response getCommentsByComment(@PathVariable("commentId") Long commentId) {
        return commentService.getAllCommentsByComment(commentId);
    }
    // v1 - 更新评论内容
    @PutMapping("/v1/posts/comments/{commentId}")
    public Response updateComment(@PathVariable("commentId") Long commentId, @RequestBody Map<String, String> args) {
        String content = args.get("content");
        return commentService.updateComment(commentId, content);
    }
    // v1 - 删除评论
    @DeleteMapping("/v1/posts/{postId}/comments/{commentId}")
    public Response deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        return null;
    }
    // v1 - 创建评论
    @PostMapping("/v1/posts/{postId}/comments")
    public Response createComment(@PathVariable("postId") Long postId, @RequestBody Map<String, String> map) {
        // 帖子评论数加一
        postService.update(new UpdateWrapper<Post>().setSql("reply_total = reply_total + 1").eq("id", postId));
        Long objectId = Long.parseLong(map.get("objectId"));
        String objectType = map.get("objectType");
        String content = map.get("content");
        return commentService.createComment(objectId, objectType, content);
    }
    // v1 - 点赞评论
    @PostMapping("/v1/posts/comments/{commentId}/likes")
    public Response createCommentLike(@PathVariable("commentId") Long commentId) {
        return commentService.likeComment(commentId);
    }
    // v1 - 取消点赞评论
    @DeleteMapping("/v1/posts/comments/{commentId}/likes")
    public Response deleteCommentLike(@PathVariable("commentId") Long commentId) {
        return commentService.dislikeComment(commentId);
    }

}
