package com.example.studycirclebackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMailEvent extends Event {
    private String to;
    private String subject;
    private String text;
    public SendMailEvent(String topic, int noticeType, String to, String subject, String text) {
        super(topic, noticeType);
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
