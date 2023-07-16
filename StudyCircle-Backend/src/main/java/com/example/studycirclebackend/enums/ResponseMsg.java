package com.example.studycirclebackend.enums;

public enum ResponseMsg {
    ERROR_PARAMETER("参数错误！"),
    NULL_OBJECT("未找到该对象！"),
    LOGIN_ERROR_DATA("邮箱或密码错误！"),
    LOGIN_SUCCESS("登录成功！");
    private String value;
    ResponseMsg(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
