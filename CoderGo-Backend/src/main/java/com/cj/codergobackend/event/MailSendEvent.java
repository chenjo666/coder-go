package com.cj.codergobackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailSendEvent extends Event {
    private String to;
    private String subject;
    private String text;
    public MailSendEvent(String noticeTopic, int noticeType, String to, String subject, String text) {
        super(noticeTopic, noticeType);
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
