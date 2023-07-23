package com.cj.studycirclebackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikePostEvent extends Event {
    private Long postId;
    private Long userId;
    public LikePostEvent(String topic, int noticeType, Long postId, Long userId) {
        super(topic, noticeType);
        this.postId = postId;
        this.userId = userId;
    }
}
