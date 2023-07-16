package com.example.studycirclebackend.util;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from); // 发件人
            helper.setTo(to);   // 收件人
            helper.setSubject(subject); // 主题
            helper.setText(text, false);   // 内容（true表示可以使用html）
            javaMailSender.send(helper.getMimeMessage());
            logger.info("send email to: " + to + " successfully!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
