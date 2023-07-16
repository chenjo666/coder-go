package com.example.studycirclebackend.enums;

public enum ActivationResult {
    SUCCESS("激活成功"),
    FAILURE("激活失败");

    private String result;

    ActivationResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
