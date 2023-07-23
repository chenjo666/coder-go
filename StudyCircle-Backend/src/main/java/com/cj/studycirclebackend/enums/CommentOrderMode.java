package com.cj.studycirclebackend.enums;

public enum CommentOrderMode {
    NORMAL("normal"),
    NEWEST("newest");

    private String value;
    CommentOrderMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
