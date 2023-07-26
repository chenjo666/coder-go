package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.constants.CommentObj;
import com.cj.studycirclebackend.constants.CommentSort;
import com.cj.studycirclebackend.constants.NoticeTopic;
import com.cj.studycirclebackend.dao.CommentMapper;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.event.*;
import com.cj.studycirclebackend.pojo.Comment;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.CommentService;
import com.cj.studycirclebackend.service.LikeService;
import com.cj.studycirclebackend.service.UserService;
import com.cj.studycirclebackend.util.DataUtil;
import com.cj.studycirclebackend.util.TextUtil;
import com.cj.studycirclebackend.util.UserUtil;
import com.cj.studycirclebackend.vo.CommentVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
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
    public Response getAllCommentsByPost(Long postId, String orderMode, Integer currentPage, Integer pageSize) {
        if (postId == null || StringUtils.isBlank(orderMode) || currentPage == null || pageSize == null) {
            return Response.badRequest();
        }
        return Response.ok(getCommentVOs(postId, orderMode, currentPage, pageSize));
    }
    @Override
    public Response getAllCommentsByComment(Long commentId) {
        List<Comment> comments = getChildCommentsByComment(commentId);
        List<CommentVO> childCommentListVO = new ArrayList<>();
        for (Comment comment :comments) {
            childCommentListVO.add(getCommentVO(comment));
        }
        return Response.ok(childCommentListVO);
    }

    /*********************************** 三个更新评论业务 ***********************************/
    @Override
    public Response createComment(Long objectId, String objectType, String content) {
        if (objectId == null || StringUtils.isBlank(objectType) || StringUtils.isBlank(content)) {
            return Response.badRequest();
        }
        Comment comment = new Comment();
        comment.setUserId(userUtil.getUser().getId());
        comment.setObjectId(objectId);
        comment.setObjectType(objectType);
        comment.setContent(TextUtil.filter(content));
        comment.setCommentTime(new Date());
        comment.setScore(0);
        save(comment);
        // 评论事件
        Event event;
        if (objectType.equals(CommentObj.POST)) {
            event = new ReplyPostEvent(NoticeTopic.COMMENT, NoticeType.REPLY_POST.getValue(), objectId, userUtil.getUser().getId());
        } else {
            event = new ReplyCommentEvent(NoticeTopic.COMMENT, NoticeType.REPLY_COMMENT.getValue(), objectId, userUtil.getUser().getId());
        }
        eventProducer.createEvent(event);

        return Response.ok();
    }
    @Override
    public Response deleteComment(Long commentId) {
        if (commentId == null) {
            return Response.badRequest();
        }
        removeById(commentId);
        return Response.ok();
    }
    @Override
    public Response updateComment(Long commentId, String newContent) {
        if (commentId == null || StringUtils.isBlank(newContent)) {
            return Response.badRequest();
        }
        update(new UpdateWrapper<Comment>().set("content", newContent).eq("id", commentId));
        return Response.ok();
    }

    /*********************************** 评论辅助工具业务 ***********************************/
    private QueryWrapper<Comment> getQueryWrapper(Long postId, String orderMode, Integer currentPage, Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
                .eq("object_type", CommentObj.POST)
                .eq("object_id", postId);
        // 综合排序规则
        CommentSort.querySort(queryWrapper, orderMode);
        queryWrapper.last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize));
        return queryWrapper;
    }
    @Override
    public List<CommentVO> getCommentVOs(Long postId, String orderMode, Integer currentPage, Integer pageSize) {
        if (postId == null || StringUtils.isBlank(orderMode) || currentPage == null || pageSize == null) {
            return null;
        }
        List<Comment> commentParentList = list(getQueryWrapper(postId, orderMode, currentPage, pageSize));
        // 外层评论排序
        commentParentList.sort(Comparator.comparing(Comment::getScore));
        // 构建外层评论视图
        List<CommentVO> parentCommentListVO = new ArrayList<>();
        for (Comment parentComment : commentParentList) {
            // 1. 父评论视图
            CommentVO commentVO = getCommentVO(parentComment);
            // 构建子评论
            // （1）全部子评论
            List<Comment> childComments = getChildCommentsByComment(parentComment.getId());
            // （2）子评论按照分数排序
            childComments.sort(Comparator.comparing(Comment::getScore));
            // （3）全部子评论视图
            List<CommentVO> childCommentVOs = new ArrayList<>();
            for (Comment childComment : childComments) {
                childCommentVOs.add(getCommentVO(childComment));
            }
            // （4）设置子评论及其数量
            commentVO.setChildCommentListVO(childCommentVOs);
            commentVO.setCommentReplies(childCommentVOs.size());
            // 2. 添加父评论
            parentCommentListVO.add(commentVO);
        }
        return parentCommentListVO;
    }
    @Override
    public List<Comment> getParentCommentsByPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return list(new QueryWrapper<Comment>()
                .eq("object_type", CommentObj.POST)
                .eq("object_id", postId));
    }
    @Override
    public List<Comment> getChildCommentsByComment(Long commentId) {
        List<Comment> ans = new ArrayList<>();
        dfs(commentId, ans);
        return ans;
    }
    private void dfs(Long commentId, List<Comment> ans) {
        List<Comment> childComments = list(new QueryWrapper<Comment>()
                .eq("object_id", commentId)
                .eq("object_type", CommentObj.COMMENT));
        if (childComments == null) {
            return;
        }
        for (Comment childComment : childComments) {
            ans.add(childComment);
            dfs(childComment.getId(), ans);
        }
    }
    // 得到帖子的评论数量 【getParentCommentsByPost】【getChildCommentsByComment】
    @Override
    public Long getPostRepliesByPostId(Long postId) {
        List<Comment> parentComments = getParentCommentsByPost(postId);
        Long commentReplyTotal = 0L;
        for (Comment comment : parentComments) {
            commentReplyTotal += getChildCommentsByComment(comment.getId()).size();
        }
        return commentReplyTotal + parentComments.size();
    }
    // 得到评论所在的帖子 id（递归实现【getPostIdByCommentId】）
    @Override
    public Long getPostIdByCommentId(Long commentId) {
        Comment comment = getById(commentId);
        if (CommentObj.POST.equals(comment.getObjectType())) {
            return comment.getObjectId();
        }
        return getPostIdByCommentId(comment.getObjectId());
    }

    /*********************************** 一个对象转换视图 ***********************************/
    public CommentVO getCommentVO(Comment comment) {
        CommentVO commentVO = new CommentVO();
        // 用户服务
        User user = userService.getById(comment.getUserId());
        commentVO.setUserId(user.getId());
        commentVO.setUserAvatar(user.getAvatar());
        commentVO.setUserName(user.getUsername());
        // 自身服务
        commentVO.setCommentId(comment.getId());
        commentVO.setCommentTime(DataUtil.formatDateTime(comment.getCommentTime()));
        commentVO.setCommentContent(comment.getContent());
        // 点赞服务
        long count = likeService.getCommentLikeTotal(comment.getId());
        boolean isLike = likeService.isLikeCommentByUser(comment.getId(), userUtil.getUser().getId());
        commentVO.setCommentLikes(Math.toIntExact(count));
        commentVO.setLike(isLike);

        commentVO.setCommentScore(comment.getScore());
        return commentVO;
    }
    /*********************************** 两个点赞相关业务 ***********************************/
    @Override
    public Response likeComment(Long commentId) {
        if (commentId == null || userUtil.getUser() == null) {
            return Response.badRequest();
        }
        likeService.createCommentLike(commentId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response dislikeComment(Long commentId) {
        if (commentId == null || userUtil.getUser() == null) {
            return Response.badRequest();
        }
        likeService.deleteCommentLike(commentId, userUtil.getUser().getId());
        return Response.ok();
    }
}
