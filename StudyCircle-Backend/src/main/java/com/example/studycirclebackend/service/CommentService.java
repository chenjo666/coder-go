package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {


    Response getCommentList(Long postId, String orderMode, Integer currentPage, Integer pageSize);


    Response addComment(Long objectId, String objectType, String content);

    Response delComment(Long commentId);

    Response setComment(Long commentId, String newContent);

    Response updateComment(Long commentId, String newContent);

    Response getCommentsByComment(Long commentId);
    List<Comment> getCommentAllByPost(Long postId);

    List<Comment> getChildCommentsByComment(Long commentId);


    /**
     * 得到帖子的评论数
     */
    Long getPostRepliesByPostId(Long postId);
    /**
     * 得到评论所在的帖子
     */
    Long getPostIdByCommentId(Long commentId);

}
