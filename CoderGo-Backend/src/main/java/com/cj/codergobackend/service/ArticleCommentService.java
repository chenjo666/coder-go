package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.ArticleComment;
import com.cj.codergobackend.vo.ArticleCommentVO;

import java.util.List;

public interface ArticleCommentService extends IService<ArticleComment> {


    Response getAllCommentsByArticle(Long articleId, String orderMode, Integer currentPage, Integer pageSize);
    Response getAllCommentsByComment(Long commentId);


    Response createComment(Long objectId, String objectType, String content);
    Response deleteComment(Long commentId);
    Response updateComment(Long commentId, String newContent);

    List<ArticleCommentVO> getCommentVOs(Long articleId, String orderMode, Integer currentPage, Integer pageSize);
    List<ArticleComment> getParentCommentsByArticle(Long articleId);
    List<ArticleComment> getChildCommentsByComment(Long commentId);

    /**
     * 得到评论所在的文章
     */
    Long getArticleIdByCommentId(Long commentId);

    ArticleCommentVO getCommentVO(ArticleComment articleComment);

    /**
     * 评论的点赞业务
     */
    Response likeComment(Long commentId);

    /**
     * 评论的取消点赞业务
     */
    Response dislikeComment(Long commentId);


    Long getCommentTotalLikes(Long userId);
}
