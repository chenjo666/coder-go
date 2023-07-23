package com.cj.studycirclebackend.enums;

public enum PostOrderMode {
    NORMAL("normal"),
    NEWEST("newest"),
    HOTEST("hotest");

    private String value;
    PostOrderMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
