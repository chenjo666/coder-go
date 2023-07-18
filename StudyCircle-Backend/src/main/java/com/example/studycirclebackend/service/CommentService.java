package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Comment;
import com.example.studycirclebackend.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {


    Response getAllCommentsByPost(Long postId, String orderMode, Integer currentPage, Integer pageSize);
    Response getAllCommentsByComment(Long commentId);


    Response createComment(Long objectId, String objectType, String content);
    Response deleteComment(Long commentId);
    Response updateComment(Long commentId, String newContent);

    List<CommentVO> getCommentVOs(Long postId, String orderMode, Integer currentPage, Integer pageSize);
    List<Comment> getParentCommentsByPost(Long postId);
    List<Comment> getChildCommentsByComment(Long commentId);


    /**
     * 得到帖子的评论数
     */
    Long getPostRepliesByPostId(Long postId);
    /**
     * 得到评论所在的帖子
     */
    Long getPostIdByCommentId(Long commentId);

    public CommentVO getCommentVO(Comment comment);

    /**
     * 评论的点赞业务
     */
    Response likeComment(Long commentId);

    /**
     * 评论的取消点赞业务
     */
    Response dislikeComment(Long commentId);

}
