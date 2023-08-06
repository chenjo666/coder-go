package com.cj.codergobackend.event;

import com.alibaba.fastjson2.JSONObject;
import com.cj.codergobackend.constants.NoticeTopic;
import com.cj.codergobackend.service.NoticeService;
import com.cj.codergobackend.util.EmailUtil;
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

    @KafkaListener(topics = NoticeTopic.ARTICLE_LIKE)
    public void handleLikeArticleTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ArticleLikeEvent event = JSONObject.parseObject(record.value().toString(), ArticleLikeEvent.class);
//        ArticleLikeEvent event = (ArticleLikeEvent) record.value();
        logger.info("like article: {}", event);
        noticeService.createLikeArticleNotice(event.getArticleId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.ARTICLE_COMMENT_LIKE)
    public void handleLikeCommentTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ArticleCommentLikeEvent event = JSONObject.parseObject(record.value().toString(), ArticleCommentLikeEvent.class);
//        ArticleCommentLikeEvent event = (ArticleCommentLikeEvent) record.value();
        logger.info("like comment: {}", event);
        noticeService.createLikeCommentNotice(event.getCommentId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.ARTICLE_REPLY)
    public void handleReplyArticleTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ArticleReplyEvent event = JSONObject.parseObject(record.value().toString(), ArticleReplyEvent.class);
//        ArticleReplyEvent event = (ArticleReplyEvent) record.value();
        logger.info("reply article: {}", event);
        noticeService.createReplyArticleNotice(event.getArticleId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.ARTICLE_COMMENT_REPLY)
    public void handleReplyCommentTopic(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ArticleCommentReplyEvent event = JSONObject.parseObject(record.value().toString(), ArticleCommentReplyEvent.class);
//        ArticleCommentReplyEvent event = (ArticleCommentReplyEvent) record.value();
        logger.info("reply comment: {}", event);
        noticeService.createReplyCommentNotice(event.getCommentId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.ARTICLE_FAVORITE)
    public void handleFavoriteEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        ArticleFavoriteEvent event = JSONObject.parseObject(record.value().toString(), ArticleFavoriteEvent.class);
//        ArticleFavoriteEvent event = (ArticleFavoriteEvent) record.value();
        logger.info("favorite article: {}", event);
        noticeService.createFavoriteArticleNotice( event.getArticleId(), event.getUserId());
    }
    @KafkaListener(topics = NoticeTopic.USER_FOLLOW)
    public void handlerFollowEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        UserFollowEvent event = JSONObject.parseObject(record.value().toString(), UserFollowEvent.class);
//        UserFollowEvent event = (UserFollowEvent) record.value();
        logger.info("follow user: {}", event);
        noticeService.createFollowUserNotice(event.getUserFromId(), event.getUserToId());
    }

    /**
     * 异步发送邮件，提高响应性能
     * @param record
     */
    @KafkaListener(topics = NoticeTopic.MAIL_SEND)
    public void handlerSendMailEvent(ConsumerRecord record) {
        if (!checkRecord(record)) {
            return;
        }
        MailSendEvent event = JSONObject.parseObject(record.value().toString(), MailSendEvent.class);
//        MailSendEvent event = (MailSendEvent) record.value();
        logger.info("send mail: {}", event);
        emailUtil.send(event.getTo(), event.getSubject(), event.getText());
    }
    private boolean checkRecord(ConsumerRecord record) {
        return record != null && record.value() != null;
    }
}
