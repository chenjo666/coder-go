package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.Notice;
import com.example.studycirclebackend.vo.NoticeVO;

import java.util.List;

public interface NoticeService extends IService<Notice> {

    boolean deleteAllNotice();

    boolean deleteSingleNotice(Long noticeId);


    boolean createLikeNotice(Long userFromId, Long objectId, String objectType);
    boolean createCommentNotice(Long userFromId, Long objectId, String objectType);
    boolean createFavoriteNotice(Long userFromId, Long postId);
    boolean createFollowNotice(Long userFromId, Long userToId);

    boolean createNotice(Long userFromId, Long userToId, Integer noticeType, Long postId);

    Response getNotice(Integer currentPage, Integer pageSize);

    NoticeVO convertToNoticeVO(Notice notice);
}
