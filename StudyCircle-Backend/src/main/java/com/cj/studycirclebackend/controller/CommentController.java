package com.cj.studycirclebackend.controller;

import com.cj.studycirclebackend.service.CommentService;
import com.cj.studycirclebackend.dto.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 根据帖子查询评论
     */
    @GetMapping("/posts/{post_id}")
    public Response getCommentsByPost(@PathVariable("post_id") Long postId, @RequestParam String orderMode,
                                   @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        return commentService.getAllCommentsByPost(postId, orderMode, currentPage, pageSize);
    }
    /**
     * 根据评论查询评论
     */
    @GetMapping("/comments/{comment_id}")
    public Response getCommentsByComment(@PathVariable("comment_id") Long commentId) {
        return commentService.getAllCommentsByComment(commentId);
    }
    /**
     * 更新评论
     */
    @PutMapping("/{comment_id}")
    public Response updateComment(@PathVariable("comment_id") Long commentId, @RequestBody Map<String, String> args) {
        String content = args.get("content");
        return commentService.updateComment(commentId, content);
    }
    /**
     * 删除评论
     */
    @DeleteMapping("/{comment_id}")
    public Response deleteComment(@PathVariable("comment_id") Long commentId) {
        return null;
    }

    /**
     * 创建评论
     */
    @PostMapping
    public Response createComment(@RequestBody Map<String, String> map) {
        Long objectId = Long.parseLong(map.get("objectId"));
        String objectType = map.get("objectType");
        String content = map.get("content");
        return commentService.createComment(objectId, objectType, content);
    }


    @PostMapping("/v1/{commentId}/likes")
    public Response createCommentLike(@PathVariable("commentId") Long commentId) {
        return commentService.likeComment(commentId);
    }

    @DeleteMapping("/v1/{commentId}/likes")
    public Response deleteCommentLike(@PathVariable("commentId") Long commentId) {
        return commentService.dislikeComment(commentId);
    }

}
