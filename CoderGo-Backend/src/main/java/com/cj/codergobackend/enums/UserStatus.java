package com.cj.codergobackend.enums;

public enum UserStatus {
    unActivated(0),
    activated(1);
    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
