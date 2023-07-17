package com.example.studycirclebackend.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Event {
    private String topic;
    private int noticeType;
}
