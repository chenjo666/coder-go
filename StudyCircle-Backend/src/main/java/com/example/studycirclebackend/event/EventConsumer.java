package com.example.studycirclebackend.event;

import com.alibaba.fastjson2.JSONObject;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.MailEvent;
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
    public void handlerLikeNotice(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        logger.info("like: {}", event);
        Long userFromId = event.getUserFromId();
        Long objectId =  event.getObjectId();
        String objectType =  event.getObjectType();
        noticeService.createLikeNotice(userFromId, objectId, objectType);
    }
    @KafkaListener(topics = Topic.COMMENT)
    public void handlerCommentEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        logger.info("comment: {}", event);
        Long userFromId = event.getUserFromId();
        Long objectId =  event.getObjectId();
        String objectType =  event.getObjectType();
        noticeService.createCommentNotice(userFromId, objectId, objectType);
    }
    @KafkaListener(topics = Topic.FAVORITE)
    public void handlerFavoriteEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        logger.info("favorite: {}", event);
        Long userFromId = event.getUserFromId();
        Long postId =  event.getPostId();
        noticeService.createFavoriteNotice(userFromId, postId);
    }
    @KafkaListener(topics = Topic.FOLLOW)
    public void handlerFollowEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        logger.info("follow: {}", event);
        Long userFromId = event.getUserFromId();
        Long userToId =  event.getUserToId();
        noticeService.createFollowNotice(userFromId, userToId);
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
