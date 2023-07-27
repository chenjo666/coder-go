package com.cj.studycirclebackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private int code; // 响应码
    private String msg; // 响应消息
    private Object data; // 数据

    public static Response ok() {
        return Response.builder().code(200).msg("OK").build();
    }
    public static Response ok(Object data) {
        return Response.builder().code(200).msg("OK").data(data).build();
    }
    public static Response created() {
        return Response.builder().code(201).msg("Created").build();
    }

    public static Response notContent() {
        return Response.builder().code(204).msg("Not Content").build();
    }

    public static Response badRequest() {
        return Response.builder().code(400).msg("Bad Request").build();
    }
    public static Response unauthorized() {
        return Response.builder().code(401).msg("Unauthorized").build();
    }

    public static Response notFound() {
        return Response.builder().code(404).msg("Not Found").build();
    }
    public static Response internalServerError() {
        return Response.builder().code(500).msg(" Internal Server Error").build();
    }

    /**
     * 登录返回值
     * @return
     */
    public static Response loginSuccess(Object data) {
        return Response.builder().code(200).msg("登录成功！").data(data).build();
    }

    public static Response loginFailed() {
        return Response.builder().code(400).msg("邮箱或密码错误！").build();
    }

    public static Response invalidOperation() {
        return Response.builder().code(400).msg("请先登录！").build();
    }



    /**
     * 注册返回值
     * @return
     */
    public static Response registerSuccess(Object data) {
        return Response.builder().code(200).msg("注册成功！").data(data).build();
    }
    public static Response registerFailed() {
        return Response.builder().code(400).msg("注册失败，请输入正确的信息！").build();
    }

    /**
     * 激活返回值
     * @return
     */
    public static Response activateSuccess() {
        return Response.builder().code(200).msg("激活成功，请查看邮箱！").build();
    }
    public static Response activateFailed() {
        return Response.builder().code(400).msg("激活失败，邮箱已经激活！").build();
    }

}
