package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Comment;
import com.cj.studycirclebackend.vo.CommentVO;

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

    CommentVO getCommentVO(Comment comment);

    /**
     * 评论的点赞业务
     */
    Response likeComment(Long commentId);

    /**
     * 评论的取消点赞业务
     */
    Response dislikeComment(Long commentId);

}
