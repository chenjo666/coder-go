package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.NoticeMapper;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.CommentObjectType;
import com.example.studycirclebackend.enums.NoticeType;
import com.example.studycirclebackend.pojo.Comment;
import com.example.studycirclebackend.pojo.Notice;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.CommentService;
import com.example.studycirclebackend.service.NoticeService;
import com.example.studycirclebackend.service.PostService;
import com.example.studycirclebackend.service.UserService;
import com.example.studycirclebackend.util.DataUtil;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.NoticeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Resource
    private UserUtil userUtil;
    @Resource
    private UserService userService;
    @Resource
    private PostService postService;
    @Resource
    private CommentService commentService;

    @Override
    public boolean deleteAllNotice() {
        if (userUtil.getUser() == null) {
            return false;
        }
        Long userId = userUtil.getUser().getId();
        return remove(new QueryWrapper<Notice>().eq("user_receiver_id", userId));
    }

    @Override
    public boolean deleteSingleNotice(Long noticeId) {
        if (noticeId == null) {
            return false;
        }
        return removeById(noticeId);
    }


    @Override
    public boolean createLikeNotice(Long userFromId, Long objectId, String objectType) {
        if (CommentObjectType.POST.getValue().equals(objectType)) {
            Post post = postService.getById(objectId);
            return createNotice(userFromId,
                    post.getUserId(),
                    NoticeType.LIKE_POST.getValue(),
                    post.getId());
        } else {
            Comment comment = commentService.getById(objectId);
            return createNotice(userFromId,
                    comment.getUserId(),
                    NoticeType.LIKE_COMMENT.getValue(),
                    commentService.getPostIdByCommentId(comment.getId()));
        }
    }

    @Override
    public boolean createCommentNotice(Long userFromId, Long objectId, String objectType) {
        if (CommentObjectType.POST.getValue().equals(objectType)) {
            Post post = postService.getById(objectId);
            return createNotice(userFromId,
                    post.getUserId(),
                    NoticeType.REPLY_POST.getValue(),
                    post.getId());
        } else {
            Comment comment = commentService.getById(objectId);
            return createNotice(userFromId,
                    comment.getUserId(),
                    NoticeType.REPLY_COMMENT.getValue(),
                    commentService.getPostIdByCommentId(comment.getId()));
        }
    }

    @Override
    public boolean createFavoriteNotice(Long userFromId, Long postId) {
        Post post = postService.getById(postId);
        return createNotice(userFromId,
                post.getUserId(),
                NoticeType.FAVORITE_POST.getValue(),
                postId);
    }

    @Override
    public boolean createFollowNotice(Long userFromId, Long userToId) {
        return createNotice(userFromId,
                userToId,
                NoticeType.FOLLOW_USER.getValue(),
                null);
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
        if (currentPage == null || pageSize == null || userUtil.getUser() == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        List<Notice> notices = list(new QueryWrapper<Notice>()
                .eq("user_receiver_id", userUtil.getUser().getId())
                .orderByDesc("notice_time")
                .last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize)));
        if (notices == null) {
            return null;
        }
        List<NoticeVO> noticeVOList = new ArrayList<>();
        for (Notice notice : notices) {
            noticeVOList.add(convertToNoticeVO(notice));
            if (notice.getIsRead() == 0) {
                notice.setIsRead(1);
            }
        }
        // 更新帖子状态
        updateBatchById(notices);
        // 帖子总数
        long count = count(new QueryWrapper<Notice>()
                .eq("user_receiver_id", userUtil.getUser().getId()));
        // 帖子总未读量
        long unRead = count(new QueryWrapper<Notice>()
                .eq("user_receiver_id", userUtil.getUser().getId())
                .eq("is_read", 0));
        data.put("noticeTotal", (int) count);
        data.put("noticeUnRead",(int) unRead);
        data.put("noticeVOList", noticeVOList);
        return Response.builder().code(200).data(data).build();
    }

    @Override
    public NoticeVO convertToNoticeVO(Notice notice) {
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
}
