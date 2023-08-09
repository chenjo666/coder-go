package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.NoticeType;
import com.cj.codergobackend.dao.NoticeMapper;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.NoticeService;
import com.cj.codergobackend.vo.NoticeVO;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.ArticleComment;
import com.cj.codergobackend.pojo.Notice;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import com.cj.codergobackend.util.UserUtil;
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
    private ArticleService articleService;
    @Resource
    private ArticleCommentService articleCommentService;

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
    public boolean createNotice(Long userFromId, Long userToId, Integer noticeType, Long articleId) {
        Notice notice = new Notice();
        notice.setUserFromId(userFromId);
        notice.setUserToId(userToId);
        notice.setNoticeType(noticeType);
        notice.setArticleId(articleId);
        notice.setCreatedAt(new Date());
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
                .orderByDesc("created_at")
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
        // 更新文章状态
        updateBatchById(notices);
        // 文章总数
        long count = count(new QueryWrapper<Notice>()
                .eq("user_to_id", userUtil.getUser().getId()));
        // 文章总未读量
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
        noticeVO.setNoticeCreatedAt(DataUtil.formatDateTime(notice.getCreatedAt()));
        noticeVO.setRead(notice.getIsRead() == 1);
        noticeVO.setNoticeType(notice.getNoticeType());

        User user = userService.getById(notice.getUserFromId());
        noticeVO.setUserId(user.getId());
        noticeVO.setUserName(user.getUsername());
        noticeVO.setUserAvatar(user.getAvatar());

        if (!notice.getNoticeType().equals(NoticeType.USER_FOLLOW)) {
            Article article = articleService.getByIdFromEs(notice.getArticleId());
            noticeVO.setArticleId(article.getId());
            noticeVO.setArticleTitle(article.getTitle());
        }
        return noticeVO;
    }

    @Override
    public boolean createLikeArticleNotice(Long articleId, Long userId) {
        Article article = articleService.getById(articleId);
        return createNotice(userId,
                article.getUserId(),
                NoticeType.ARTICLE_LIKE,
                article.getId());
    }

    @Override
    public boolean createLikeCommentNotice(Long commentId, Long userId) {
        ArticleComment articleComment = articleCommentService.getById(commentId);
        return createNotice(userId,
                articleComment.getUserId(),
                NoticeType.ARTICLE_COMMENT_LIKE,
                articleCommentService.getArticleIdByCommentId(articleComment.getId()));
    }

    @Override
    public boolean createReplyArticleNotice(Long articleId, Long userId) {
        Article article = articleService.getById(articleId);
        return createNotice(userId,
                article.getUserId(),
                NoticeType.ARTICLE_REPLY,
                articleId);
    }

    @Override
    public boolean createReplyCommentNotice(Long commentId, Long userId) {
        ArticleComment comment = articleCommentService.getById(commentId);
        Long articleId = articleCommentService.getArticleIdByCommentId(commentId);
        return createNotice(userId,
                comment.getUserId(),
                NoticeType.ARTICLE_COMMENT_REPLY,
                articleId);
    }

    @Override
    public boolean createFavoriteArticleNotice(Long articleId, Long userId) {
        Article article = articleService.getById(articleId);
        return createNotice(userId,
                article.getUserId(),
                NoticeType.ARTICLE_FAVORITE,
                articleId);
    }

    @Override
    public boolean createFollowUserNotice(Long userFromId, Long userToId) {
        return createNotice(userFromId,
                userToId,
                NoticeType.USER_FOLLOW,
                null);
    }

}
