package com.example.studycirclebackend.enums;

public enum UserType {
    ADMIN(0),
    NORMAL(1);
    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
