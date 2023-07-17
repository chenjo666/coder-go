package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.CommentService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 根据帖子查询评论
     * @param postId
     * @param orderMode
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/posts/{post_id}")
    public Response getCommentsByPost(@PathVariable("post_id") Long postId, @RequestParam String orderMode,
                                   @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        return commentService.getCommentList(postId, orderMode, currentPage, pageSize);
    }
    /**
     * 根据评论查询评论
     * @param commentId
     * @return
     */
    @GetMapping("/comments/{comment_id}")
    public Response getCommentsByComment(@PathVariable("comment_id") Long commentId) {
        return commentService.getCommentsByComment(commentId);
    }
    /**
     * 更新评论
     * @param commentId
     * @param args
     * @return
     */
    @PutMapping("/{comment_id}")
    public Response updateComment(@PathVariable("comment_id") Long commentId, @RequestBody Map<String, String> args) {
        String content = args.get("content");
        return commentService.setComment(commentId, content);
    }
    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @DeleteMapping("/{comment_id}")
    public Response deleteComment(@PathVariable("comment_id") Long commentId) {
        return null;
    }

    /**
     * 创建评论
     * @param map
     * @return
     */
    @PostMapping
    public Response createComment(@RequestBody Map<String, String> map) {
        Long objectId = Long.parseLong(map.get("objectId"));
        String objectType = map.get("objectType");
        String content = map.get("content");
        return commentService.addComment(objectId, objectType, content);
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
