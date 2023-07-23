package com.cj.studycirclebackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplyCommentEvent extends Event{
    private Long commentId;
    private Long userId;

    public ReplyCommentEvent(String topic, int noticeType, Long commentId, Long userId) {
        super(topic, noticeType);
        this.commentId = commentId;
        this.userId = userId;
    }
}
