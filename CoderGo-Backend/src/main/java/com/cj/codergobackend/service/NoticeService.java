package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.vo.NoticeVO;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Notice;

public interface NoticeService extends IService<Notice> {

    Response deleteAllNotice();

    Response deleteNotice(Long noticeId);


    boolean createNotice(Long userFromId, Long userToId, Integer noticeType, Long articleId);

    Response getNotice(Integer currentPage, Integer pageSize);

    NoticeVO getNoticeVO(Notice notice);

    boolean createLikeArticleNotice(Long articleId, Long userId);
    boolean createLikeCommentNotice(Long commentId, Long userId);
    boolean createReplyArticleNotice(Long articleId, Long userId);
    boolean createReplyCommentNotice(Long commentId, Long userId);
    boolean createFavoriteArticleNotice(Long articleId, Long userId);
    boolean createFollowUserNotice(Long userFromId, Long userToId);
}
