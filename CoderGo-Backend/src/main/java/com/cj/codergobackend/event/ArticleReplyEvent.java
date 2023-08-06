package com.cj.codergobackend.event;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleReplyEvent extends Event {
    private Long articleId;
    private Long userId;
    public ArticleReplyEvent(String noticeTopic, int noticeType, Long articleId, Long userId) {
        super(noticeTopic, noticeType);
        this.articleId = articleId;
        this.userId = userId;
    }
}
