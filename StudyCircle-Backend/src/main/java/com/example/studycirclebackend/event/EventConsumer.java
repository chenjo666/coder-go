package com.example.studycirclebackend.event;

import com.alibaba.fastjson2.JSONObject;
import com.example.studycirclebackend.enums.NoticeType;
import com.example.studycirclebackend.service.NoticeService;
import com.example.studycirclebackend.util.EmailUtil;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Resource
    private NoticeService noticeService;
    @Resource
    private EmailUtil emailUtil;

    @KafkaListener(topics = Topic.LIKE)
    public void handleLikeTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(record.value().toString());
        int noticeType = jsonObject.getInteger("noticeType");
        logger.info("like: {}", jsonObject);
        if (noticeType == NoticeType.LIKE_POST.getValue()) {
            LikePostEvent likePostEvent = jsonObject.toJavaObject(LikePostEvent.class);
            Long postId = likePostEvent.getPostId();
            Long userId = likePostEvent.getUserId();
            noticeService.createLikePostNotice(postId, userId);
        } else {
            LikeCommentEvent likeCommentEvent = jsonObject.toJavaObject(LikeCommentEvent.class);
            Long commentId = likeCommentEvent.getCommentId();
            Long userId = likeCommentEvent.getUserId();
            noticeService.createLikeCommentNotice(commentId, userId);
        }
    }
    @KafkaListener(topics = Topic.COMMENT)
    public void handleCommentEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(record.value().toString());
        int noticeType = jsonObject.getInteger("noticeType");
        logger.info("comment: {}", jsonObject);
        if (noticeType == NoticeType.REPLY_POST.getValue()) {
            ReplyPostEvent event0 = jsonObject.toJavaObject(ReplyPostEvent.class);
            Long postId = event0.getPostId();
            Long userId = event0.getUserId();
            noticeService.createReplyPostNotice(postId, userId);
        } else {
            ReplyCommentEvent event0 = jsonObject.toJavaObject(ReplyCommentEvent.class);
            Long commentId = event0.getCommentId();
            Long userId = event0.getUserId();
            noticeService.createReplyCommentNotice(commentId, userId);
        }
    }
    @KafkaListener(topics = Topic.FAVORITE)
    public void handleFavoriteEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        FavoritePostEvent favoritePostEvent = JSONObject.parseObject(record.value().toString(), FavoritePostEvent.class);
        logger.info("favorite: {}", favoritePostEvent);
        Long postId = favoritePostEvent.getPostId();
        Long userId =  favoritePostEvent.getUserId();
        noticeService.createFavoritePostNotice(postId, userId);
    }
    @KafkaListener(topics = Topic.FOLLOW)
    public void handlerFollowEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        FollowUserEvent followUserEvent = JSONObject.parseObject(record.value().toString(), FollowUserEvent.class);
        logger.info("follow: {}", followUserEvent);
        Long userFromId = followUserEvent.getUserFromId();
        Long userToId =  followUserEvent.getUserToId();
        noticeService.createFollowUserNotice(userFromId, userToId);
    }

    /**
     * 异步发送邮件，提高响应性能
     * @param record
     */
    @KafkaListener(topics = Topic.MAIL)
    public void handlerSendMailEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        logger.info("follow: {}", event);
        MailEvent mailEvent = JSONObject.parseObject(record.value().toString(), MailEvent.class);
        emailUtil.send(mailEvent.getTo(), mailEvent.getSubject(), mailEvent.getText());
    }
    private boolean checkRecord(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            return false;
        }
        return true;
    }
}
