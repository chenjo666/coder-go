package com.cj.codergobackend.enums;

public enum MessageRole {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    FUNCTION("function");

    private String value;
    MessageRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
