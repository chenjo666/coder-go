package com.example.studycirclebackend.enums;

public enum Topic {
    LIKE("like"),
    COMMENT("comment"),
    FAVORITE("favorite"),
    FOLLOW("follow"),
    EMAIL("email");

    public final String value;
    Topic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
