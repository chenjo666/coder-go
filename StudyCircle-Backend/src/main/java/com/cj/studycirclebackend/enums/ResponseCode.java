package com.cj.studycirclebackend.enums;

public enum ResponseCode {
    SUCCESS(200),
    CREATED(201),
    NO_CONTENT(204),
    NOT_FOUND(404),
    FAILURE(-1);
    private final int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
