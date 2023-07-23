package com.cj.studycirclebackend.event;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplyPostEvent extends Event {
    private Long postId;
    private Long userId;
    public ReplyPostEvent(String topic, int noticeType, Long postId, Long userId) {
        super(topic, noticeType);
        this.postId = postId;
        this.userId = userId;
    }
}
