package com.cj.studycirclebackend.enums;

public enum CommentObjectType {
    POST("post"),
    COMMENT("comment");

    private String value;
    CommentObjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
