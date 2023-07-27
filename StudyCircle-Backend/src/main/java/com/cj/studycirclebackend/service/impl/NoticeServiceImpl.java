package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.dao.NoticeMapper;
import com.cj.studycirclebackend.enums.NoticeType;
import com.cj.studycirclebackend.service.CommentService;
import com.cj.studycirclebackend.service.NoticeService;
import com.cj.studycirclebackend.vo.NoticeVO;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Comment;
import com.cj.studycirclebackend.pojo.Notice;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.service.PostService;
import com.cj.studycirclebackend.service.UserService;
import com.cj.studycirclebackend.util.DataUtil;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Resource
    private UserUtil userUtil;
    @Resource
    private UserService userService;
    @Resource
    private PostService postService;
    @Resource
    private CommentService commentService;

    @Override
    public Response deleteAllNotice() {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        boolean res = remove(new QueryWrapper<Notice>().eq("user_to_id", userUtil.getUser().getId()));
        return res ? Response.ok() : Response.notContent();
    }

    @Override
    public Response deleteNotice(Long noticeId) {
        boolean res = removeById(noticeId);
        return res ? Response.ok() : Response.notContent();
    }

    @Override
    public boolean createNotice(Long userFromId, Long userToId, Integer noticeType, Long postId) {
        Notice notice = new Notice();
        notice.setUserFromId(userFromId);
        notice.setUserToId(userToId);
        notice.setNoticeType(noticeType);
        notice.setPostId(postId);
        notice.setNoticeTime(new Date());
        notice.setIsRead(0);
        return save(notice);
    }

    @Override
    @Transactional
    public Response getNotice(Integer currentPage, Integer pageSize) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        logger.info("user: {}", userUtil.getUser());
        Map<String, Object> data = new HashMap<>();
        List<Notice> notices = list(new QueryWrapper<Notice>()
                .eq("user_to_id", userUtil.getUser().getId())
                .orderByDesc("notice_time")
                .last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize)));
        if (notices == null) {
            return Response.notContent();
        }
        List<NoticeVO> noticeVOList = new ArrayList<>();
        for (Notice notice : notices) {
            noticeVOList.add(getNoticeVO(notice));
            if (notice.getIsRead() == 0) {
                notice.setIsRead(1);
            }
        }
        // 更新帖子状态
        updateBatchById(notices);
        // 帖子总数
        long count = count(new QueryWrapper<Notice>()
                .eq("user_to_id", userUtil.getUser().getId()));
        // 帖子总未读量
        long unRead = count(new QueryWrapper<Notice>()
                .eq("user_to_id", userUtil.getUser().getId())
                .eq("is_read", 0));
        data.put("noticeTotal", (int) count);
        data.put("noticeUnRead", (int) unRead);
        data.put("noticeVOList", noticeVOList);
        return Response.ok(data);
    }

    @Override
    public NoticeVO getNoticeVO(Notice notice) {
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setNoticeId(notice.getId());
        noticeVO.setNoticeTime(DataUtil.formatDateTime(notice.getNoticeTime()));
        noticeVO.setRead(notice.getIsRead() == 1);
        noticeVO.setNoticeType(notice.getNoticeType());

        User user = userService.getById(notice.getUserFromId());
        noticeVO.setUserId(user.getId());
        noticeVO.setUserName(user.getUsername());
        noticeVO.setUserAvatar(user.getAvatar());

        if (!notice.getNoticeType().equals(NoticeType.FOLLOW_USER.getValue())) {
            Post post = postService.getById(notice.getPostId());
            noticeVO.setPostId(post.getId());
            noticeVO.setPostTitle(post.getTitle());
        }
        return noticeVO;
    }

    @Override
    public boolean createLikePostNotice(Long postId, Long userId) {
        Post post = postService.getById(postId);
        return createNotice(userId,
                post.getUserId(),
                NoticeType.LIKE_POST.getValue(),
                post.getId());
    }

    @Override
    public boolean createLikeCommentNotice(Long commentId, Long userId) {
        Comment comment = commentService.getById(commentId);
        return createNotice(userId,
                comment.getUserId(),
                NoticeType.LIKE_COMMENT.getValue(),
                commentService.getPostIdByCommentId(comment.getId()));
    }

    @Override
    public boolean createReplyPostNotice(Long postId, Long userId) {
        return createNotice(userId,
                postId,
                NoticeType.REPLY_POST.getValue(),
                postId);
    }

    @Override
    public boolean createReplyCommentNotice(Long commentId, Long userId) {
        Long postId = commentService.getPostIdByCommentId(commentId);
        return createNotice(userId,
                postId,
                NoticeType.REPLY_COMMENT.getValue(),
                postId);
    }

    @Override
    public boolean createFavoritePostNotice(Long postId, Long userId) {
        Post post = postService.getById(postId);
        return createNotice(userId,
                post.getUserId(),
                NoticeType.FAVORITE_POST.getValue(),
                postId);
    }

    @Override
    public boolean createFollowUserNotice(Long userFromId, Long userToId) {
        return createNotice(userFromId,
                userToId,
                NoticeType.FOLLOW_USER.getValue(),
                null);
    }

}
