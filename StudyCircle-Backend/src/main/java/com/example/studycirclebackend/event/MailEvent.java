package com.example.studycirclebackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailEvent extends Event {
    private String to;
    private String subject;
    private String text;
    public MailEvent(String topic, int noticeType, String to, String subject, String text) {
        super(topic, noticeType);
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
