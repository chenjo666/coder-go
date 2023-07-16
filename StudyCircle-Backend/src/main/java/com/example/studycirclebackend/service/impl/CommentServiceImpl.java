package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.CommentMapper;
import com.example.studycirclebackend.dao.UserMapper;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.*;
import com.example.studycirclebackend.event.EventProducer;
import com.example.studycirclebackend.pojo.Comment;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.CommentService;
import com.example.studycirclebackend.service.LikeService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.DataUtil;
import com.example.studycirclebackend.util.TextUtil;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.CommentVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserUtil userUtil;
    @Resource
    private LikeService likeService;
    @Resource
    private EventProducer eventProducer;
    @Override
    public Response getCommentList(Long postId, String orderMode, Integer currentPage, Integer pageSize) {
        if (postId == null || StringUtils.isBlank(orderMode) || currentPage == null || pageSize == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        List<Comment> commentParentList = null;
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>()
                .eq("object_type", CommentObjectType.POST.getValue())
                .eq("object_id", postId);
        // 综合排序规则
        if (CommentOrderMode.NORMAL.getValue().equals(orderMode)) {
            queryWrapper.orderByDesc("score");
        } else if (CommentOrderMode.NEWEST.getValue().equals(orderMode)) {
            queryWrapper.orderByDesc("comment_time");
        }
        queryWrapper.last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize));
        commentParentList = list(queryWrapper);

        List<CommentVO> parentCommentListVO = new ArrayList<>();

        if (commentParentList != null) {
            for (Comment comment : commentParentList) {
                CommentVO commentVO = new CommentVO();

                User user = userService.getById(comment.getUserId());
                // 作者
                commentVO.setUserId(user.getId());
                commentVO.setUserAvatar(user.getAvatar());
                commentVO.setUserName(user.getUsername());
                // 评论
                commentVO.setCommentId(comment.getId());
                commentVO.setCommentTime(DataUtil.formatDateTime(comment.getCommentTime()));
                commentVO.setCommentContent(comment.getContent());
                commentVO.setCommentScore(comment.getScore());
                // 点赞

                long count = likeService.getLikeCountByObject(comment.getId(), CommentObjectType.COMMENT.getValue());
                boolean isLike = likeService.isLikedByUser(userUtil.getUser().getId(), comment.getId(), CommentObjectType.COMMENT.getValue());
                commentVO.setCommentLikes(Math.toIntExact(count));
                commentVO.setLike(isLike);
                // 子评论
                List<CommentVO> childCommentListVO = new ArrayList<>();
                dfs(comment, childCommentListVO);
                // 子评论按照分数排序
                childCommentListVO.sort(Comparator.comparing(CommentVO::getCommentScore));
                commentVO.setChildCommentListVO(childCommentListVO);
                commentVO.setCommentReplies(childCommentListVO.size());
                parentCommentListVO.add(commentVO);
            }
        }
        return Response.builder().code(200).data(parentCommentListVO).build();
    }

    @Override
    public Response addComment(Long objectId, String objectType, String content) {
        if (objectId == null || StringUtils.isBlank(objectType) || StringUtils.isBlank(content)) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        Comment comment = new Comment();
        comment.setUserId(userUtil.getUser().getId());
        comment.setObjectId(objectId);
        comment.setObjectType(objectType);
        comment.setContent(TextUtil.filter(content));
        comment.setCommentTime(new Date());
        comment.setScore(0);
        boolean result = save(comment);
        // 评论事件
        Event event = Event.builder()
                .topic("comment")
                .userFromId(userUtil.getUser().getId())
                .objectId(objectId)
                .objectType(objectType)
                .build();
        eventProducer.createEvent(event);

        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response delComment(Long commentId) {
        if (commentId == null) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }
        boolean result = removeById(commentId);
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response setComment(Long commentId, String newContent) {
        if (commentId == null || StringUtils.isBlank(newContent)) {
            return Response.builder()
                    .code(ResponseCode.FAILURE.getValue())
                    .msg(ResponseMsg.ERROR_PARAMETER.getValue())
                    .build();
        }

        boolean result = update(new UpdateWrapper<Comment>().set("content", newContent).eq("id", commentId));
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response updateComment(Long commentId, String newContent) {
        if (commentId == null || StringUtils.isBlank(newContent)) {
            return Response.builder()
                    .code(400)
                    .msg("Bad Request")
                    .build();
        }

        boolean result = update(new UpdateWrapper<Comment>().set("content", newContent).eq("id", commentId));
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public Response getCommentsByComment(Long commentId) {
        List<Comment> comments = getChildCommentsByComment(commentId);
        List<CommentVO> childCommentListVO = new ArrayList<>();
        for (Comment comment :comments) {
            childCommentListVO.add(getCommentVO(comment));
        }
        return Response.builder().code(200).data(childCommentListVO).build();
    }

    private CommentVO getCommentVO(Comment comment) {
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
        long count = likeService.getLikeCountByObject(comment.getId(), CommentObjectType.COMMENT.getValue());
        boolean isLike = likeService.isLikedByUser(userUtil.getUser().getId(), comment.getId(), CommentObjectType.COMMENT.getValue());
        commentVO.setCommentLikes(Math.toIntExact(count));
        commentVO.setLike(isLike);

        commentVO.setCommentScore(comment.getScore());
        return commentVO;
    }
    @Override
    public List<Comment> getCommentAllByPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return list(new QueryWrapper<Comment>()
                .eq("object_type", CommentObjectType.POST.getValue())
                .eq("object_id", postId));
    }

    @Override
    public List<Comment> getChildCommentsByComment(Long commentId) {
        List<Comment> ans = new ArrayList<>();
        dfs(commentId, ans);
        return ans;
    }

    @Override
    public Long getPostRepliesByPostId(Long postId) {
        List<Comment> parentComments = getCommentAllByPost(postId);
        Long commentReplyTotal = 0L;
        for (Comment comment : parentComments) {
            commentReplyTotal += getChildCommentsByComment(comment.getId()).size();
        }
        return commentReplyTotal + parentComments.size();
    }


    private void dfs(Long commentId, List<Comment> ans) {
        List<Comment> childComments = list(new QueryWrapper<Comment>()
                .eq("object_id", commentId)
                .eq("object_type", CommentObjectType.COMMENT.getValue()));
        if (childComments == null) {
            return;
        }
        for (Comment childComment : childComments) {
            ans.add(childComment);
            dfs(childComment.getId(), ans);
        }
    }



    private void dfs(Comment commentParent, List<CommentVO> childCommentListVO) {
        List<Comment> childComments = list(new QueryWrapper<Comment>().
                eq("object_type", CommentObjectType.COMMENT.getValue()).
                eq("object_id", commentParent.getId()));
        if (childComments == null) {
            return;
        }
        for (Comment comment : childComments) {
            CommentVO commentVO = new CommentVO();

            User user = userMapper.selectById(comment.getUserId());
            commentVO.setUserId(user.getId());
            commentVO.setUserAvatar(user.getAvatar());
            commentVO.setUserName(user.getUsername());

            commentVO.setCommentId(comment.getId());
            commentVO.setCommentTime(DataUtil.formatDateTime(comment.getCommentTime()));
            commentVO.setCommentContent(comment.getContent());

            long count = likeService.getLikeCountByObject(comment.getId(), CommentObjectType.COMMENT.getValue());
            boolean isLike = likeService.isLikedByUser(userUtil.getUser().getId(), comment.getId(), CommentObjectType.COMMENT.getValue());
            commentVO.setCommentLikes(Math.toIntExact(count));
            commentVO.setLike(isLike);
            commentVO.setCommentScore(comment.getScore());

            dfs(comment, childCommentListVO);
            childCommentListVO.add(commentVO);
        }
    }


    /**
     * 得到评论所在的帖子 id（递归实现）
     * @param commentId
     * @return
     */
    @Override
    public Long getPostIdByCommentId(Long commentId) {
        Comment comment = getById(commentId);
        if (CommentObjectType.POST.getValue().equals(comment.getObjectType())) {
            return comment.getObjectId();
        }
        return getPostIdByCommentId(comment.getObjectId());
    }
}
