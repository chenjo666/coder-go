package com.cj.studycirclebackend.event;

import com.alibaba.fastjson2.JSONObject;
import com.cj.studycirclebackend.constants.NoticeTopic;
import com.cj.studycirclebackend.service.NoticeService;
import com.cj.studycirclebackend.util.EmailUtil;
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

    @KafkaListener(topics = NoticeTopic.LIKE_POST)
    public void handleLikePostTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        LikePostEvent event = JSONObject.parseObject(record.value().toString(), LikePostEvent.class);
        logger.info("like post: {}", event);
        noticeService.createLikePostNotice(event.getPostId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.LIKE_COMMENT)
    public void handleLikeCommentTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        LikeCommentEvent event = JSONObject.parseObject(record.value().toString(), LikeCommentEvent.class);
        logger.info("like comment: {}", event);
        noticeService.createLikeCommentNotice(event.getCommentId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.REPLY_POST)
    public void handleReplyPostTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ReplyPostEvent event = JSONObject.parseObject(record.value().toString(), ReplyPostEvent.class);
        logger.info("reply post: {}", event);
        noticeService.createReplyPostNotice(event.getPostId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.REPLY_COMMENT)
    public void handleReplyCommentTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ReplyCommentEvent event = JSONObject.parseObject(record.value().toString(), ReplyCommentEvent.class);
        logger.info("reply comment: {}", event);
        noticeService.createReplyCommentNotice(event.getCommentId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.FAVORITE)
    public void handleFavoriteEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        FavoritePostEvent event = JSONObject.parseObject(record.value().toString(), FavoritePostEvent.class);
        logger.info("favorite post: {}", event);
        noticeService.createFavoritePostNotice( event.getPostId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.FOLLOW)
    public void handlerFollowEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        FollowUserEvent event = JSONObject.parseObject(record.value().toString(), FollowUserEvent.class);
        logger.info("follow user: {}", event);
        noticeService.createFollowUserNotice(event.getUserFromId(), event.getUserToId());
    }

    /**
     * 异步发送邮件，提高响应性能
     * @param record
     */
    @KafkaListener(topics = NoticeTopic.MAIL)
    public void handlerSendMailEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        SendMailEvent event = JSONObject.parseObject(record.value().toString(), SendMailEvent.class);
        logger.info("send mail: {}", event);
        emailUtil.send(event.getTo(), event.getSubject(), event.getText());
    }
    private boolean checkRecord(ConsumerRecord record) {
        return record != null && record.value() != null;
    }
}
