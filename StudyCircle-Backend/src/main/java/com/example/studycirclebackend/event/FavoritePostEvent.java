package com.example.studycirclebackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoritePostEvent extends Event {
    private Long postId;
    private Long userId;
    public FavoritePostEvent(String topic, int noticeType, Long postId, Long userId) {
        super(topic, noticeType);
        this.postId = postId;
        this.userId = userId;
    }
}
