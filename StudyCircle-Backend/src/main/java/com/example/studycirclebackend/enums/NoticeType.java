package com.example.studycirclebackend.enums;

public enum NoticeType {
    LIKE_POST(1),
    LIKE_COMMENT(2),
    REPLY_POST(3),
    REPLY_COMMENT(4),
    FAVORITE_POST(5),
    FOLLOW_USER(6),
    SEND_MAIL(7);
    private final int value;

    NoticeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
