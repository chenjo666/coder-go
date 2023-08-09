package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.ArticleCommentObj;
import com.cj.codergobackend.constants.ArticleCommentSort;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.dao.ArticleCommentMapper;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.event.*;
import com.cj.codergobackend.pojo.ArticleComment;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.LikeService;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import com.cj.codergobackend.util.TextUtil;
import com.cj.codergobackend.util.UserUtil;
import com.cj.codergobackend.vo.ArticleCommentVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements ArticleCommentService {
    @Resource
    private UserService userService;
    @Resource
    private UserUtil userUtil;
    @Resource
    private LikeService likeService;
    @Resource
    private EventProducer eventProducer;
    /*********************************** 两个查询评论业务 ***********************************/
    @Override
    public Response getAllCommentsByArticle(Long articleId, String orderMode, Integer currentPage, Integer pageSize) {
        List<ArticleCommentVO> articleCommentVOS = getCommentVOs(articleId, orderMode, currentPage, pageSize);
        return articleCommentVOS == null ? Response.notFound() : Response.ok(articleCommentVOS);
    }
    @Override
    public Response getAllCommentsByComment(Long commentId) {
        List<ArticleComment> articleComments = getChildCommentsByComment(commentId);
        if (articleComments == null) {
            return Response.notFound();
        }
        List<ArticleCommentVO> childCommentListVO = new ArrayList<>(articleComments.size());
        for (ArticleComment articleComment : articleComments) {
            childCommentListVO.add(getCommentVO(articleComment));
        }
        childCommentListVO.sort((o1, o2) -> o2.getCommentCreatedAt().compareTo(o1.getCommentCreatedAt()));
        return Response.ok(childCommentListVO);
    }

    /*********************************** 三个更新评论业务 ***********************************/
    @Override
    public Response createComment(Long objectId, String objectType, String content) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        // 检验文章/评论是否存在


        ArticleComment articleComment = new ArticleComment();
        articleComment.setUserId(userUtil.getUser().getId());
        articleComment.setObjectId(objectId);
        articleComment.setObjectType(objectType);
        articleComment.setContent(TextUtil.filter(content));
        articleComment.setCreatedAt(new Date());
        articleComment.setScore(0);
        boolean res = save(articleComment);
        // 评论事件
        Event event;
        if (objectType.equals(ArticleCommentObj.ARTICLE)) {
            event = new ArticleReplyEvent(NoticeTopic.ARTICLE_REPLY, NoticeType.ARTICLE_REPLY, objectId, userUtil.getUser().getId());
        } else {
            event = new ArticleCommentReplyEvent(NoticeTopic.ARTICLE_COMMENT_REPLY, NoticeType.ARTICLE_COMMENT_REPLY, objectId, userUtil.getUser().getId());
        }
        eventProducer.createEvent(event);

        return res ? Response.created() : Response.internalServerError();
    }
    @Override
    public Response deleteComment(Long commentId) {
        boolean res = removeById(commentId);
        return res ? Response.notContent() : Response.notFound();
    }
    @Override
    public Response updateComment(Long commentId, String newContent) {
        boolean res = update(new UpdateWrapper<ArticleComment>().set("content", newContent).eq("id", commentId));
        return res ? Response.ok() : Response.notFound();
    }

    /*********************************** 评论辅助工具业务 ***********************************/
    private QueryWrapper<ArticleComment> getQueryWrapper(Long articleId, String orderMode, Integer currentPage, Integer pageSize) {
        QueryWrapper<ArticleComment> queryWrapper = new QueryWrapper<ArticleComment>()
                .eq("object_type", ArticleCommentObj.ARTICLE)
                .eq("object_id", articleId);
        // 综合排序规则
        ArticleCommentSort.querySort(queryWrapper, orderMode);
        queryWrapper.last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize));
        return queryWrapper;
    }
    @Override
    public List<ArticleCommentVO> getCommentVOs(Long articleId, String orderMode, Integer currentPage, Integer pageSize) {
        List<ArticleComment> articleCommentParentList = list(getQueryWrapper(articleId, orderMode, currentPage, pageSize));
        if (articleCommentParentList == null) {
            return null;
        }
        // 外层评论排序
        articleCommentParentList.sort(Comparator.comparing(ArticleComment::getScore));
        // 构建外层评论视图
        List<ArticleCommentVO> parentCommentListVO = new ArrayList<>();
        for (ArticleComment parentArticleComment : articleCommentParentList) {
            // 1. 父评论视图
            ArticleCommentVO articleCommentVO = getCommentVO(parentArticleComment);
            // 构建子评论
            // （1）全部子评论
            List<ArticleComment> childArticleComments = getChildCommentsByComment(parentArticleComment.getId());
            // （2）子评论按照分数排序
            childArticleComments.sort(Comparator.comparing(ArticleComment::getScore));
            // （3）全部子评论视图
            List<ArticleCommentVO> childArticleCommentVOS = new ArrayList<>();
            for (ArticleComment childArticleComment : childArticleComments) {
                childArticleCommentVOS.add(getCommentVO(childArticleComment));
            }
            // （4）设置子评论及其数量
            articleCommentVO.setChildCommentListVO(childArticleCommentVOS);
            articleCommentVO.setTotalReplies(childArticleCommentVOS.size());
            // 2. 添加父评论
            parentCommentListVO.add(articleCommentVO);
        }
        return parentCommentListVO;
    }
    @Override
    public List<ArticleComment> getParentCommentsByArticle(Long articleId) {
        if (articleId == null) {
            return null;
        }
        return list(new QueryWrapper<ArticleComment>()
                .eq("object_type", ArticleCommentObj.ARTICLE)
                .eq("object_id", articleId));
    }
    @Override
    public List<ArticleComment> getChildCommentsByComment(Long commentId) {
        List<ArticleComment> ans = new ArrayList<>();
        dfs(commentId, ans);
        return ans;
    }
    private void dfs(Long commentId, List<ArticleComment> ans) {
        List<ArticleComment> childArticleComments = list(new QueryWrapper<ArticleComment>()
                .eq("object_id", commentId)
                .eq("object_type", ArticleCommentObj.COMMENT));
        if (childArticleComments == null) {
            return;
        }
        for (ArticleComment childArticleComment : childArticleComments) {
            ans.add(childArticleComment);
            dfs(childArticleComment.getId(), ans);
        }
    }

    // 得到评论所在的文章 id（递归实现【getArticleIdByCommentId】）
    @Override
    public Long getArticleIdByCommentId(Long commentId) {
        ArticleComment articleComment = getById(commentId);
        if (ArticleCommentObj.ARTICLE.equals(articleComment.getObjectType())) {
            return articleComment.getObjectId();
        }
        return getArticleIdByCommentId(articleComment.getObjectId());
    }

    /*********************************** 一个对象转换视图 ***********************************/
    public ArticleCommentVO getCommentVO(ArticleComment articleComment) {
        ArticleCommentVO articleCommentVO = new ArticleCommentVO();
        // 用户服务
        User user = userService.getById(articleComment.getUserId());
        articleCommentVO.setUserId(user.getId());
        articleCommentVO.setUserAvatar(user.getAvatar());
        articleCommentVO.setUserName(user.getUsername());
        // 自身服务
        articleCommentVO.setCommentId(articleComment.getId());
        articleCommentVO.setCommentCreatedAt(DataUtil.formatDateTime(articleComment.getCreatedAt()));
        articleCommentVO.setCommentContent(articleComment.getContent());
        // 点赞服务
        long count = likeService.getArticleCommentLikeTotal(articleComment.getId());
        articleCommentVO.setTotalLikes(Math.toIntExact(count));
        if (userUtil.getUser() == null) {
            articleCommentVO.setLike(false);
            return articleCommentVO;
        }
        boolean isLike = likeService.isLikeArticleCommentByUser(articleComment.getId(), userUtil.getUser().getId());
        articleCommentVO.setLike(isLike);
        return articleCommentVO;
    }
    /*********************************** 两个点赞相关业务 ***********************************/
    @Override
    public Response likeComment(Long commentId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        likeService.createArticleCommentLike(commentId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response dislikeComment(Long commentId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        likeService.deleteArticleCommentLike(commentId, userUtil.getUser().getId());
        return Response.ok();
    }

    @Override
    public Long getCommentTotalLikes(Long userId) {
        // 1. 得到全部评论
        List<ArticleComment> comments = list(new QueryWrapper<ArticleComment>().eq("user_id", userId));
        // 2. 得到评论的点赞数量
        Long totalLikes = 0L;
        for (ArticleComment comment : comments) {
            Long likes = likeService.getArticleCommentLikeTotal(comment.getId());
            totalLikes += likes;
        }
        return totalLikes;
    }
}
