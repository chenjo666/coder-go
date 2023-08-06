package com.cj.codergobackend.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleCommentLikeEvent extends Event {
    private Long commentId;
    private Long userId;
    public ArticleCommentLikeEvent(String noticeTopic, int noticeType, Long commentId, Long userId) {
        super(noticeTopic, noticeType);
        this.commentId = commentId;
        this.userId = userId;
    }
}
