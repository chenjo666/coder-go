package com.cj.codergobackend.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Event {
    private String noticeTopic;
    private int noticeType;
}
