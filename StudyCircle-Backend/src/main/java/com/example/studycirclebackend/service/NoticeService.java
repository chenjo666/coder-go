package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Notice;
import com.example.studycirclebackend.vo.NoticeVO;

public interface NoticeService extends IService<Notice> {

    boolean deleteAllNotice();

    boolean deleteNotice(Long noticeId);


    boolean createNotice(Long userFromId, Long userToId, Integer noticeType, Long postId);

    Response getNotice(Integer currentPage, Integer pageSize);

    NoticeVO convertToNoticeVO(Notice notice);

    boolean createLikePostNotice(Long postId, Long userId);
    boolean createLikeCommentNotice(Long commentId, Long userId);
    boolean createReplyPostNotice(Long postId, Long userId);
    boolean createReplyCommentNotice(Long commentId, Long userId);
    boolean createFavoritePostNotice(Long postId, Long userId);
    boolean createFollowUserNotice(Long userFromId, Long userToId);
}
