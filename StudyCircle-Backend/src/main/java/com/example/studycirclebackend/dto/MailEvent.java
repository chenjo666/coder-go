package com.example.studycirclebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailEvent {
    private String topic;
    private String to;
    private String subject;
    private String text;
}
