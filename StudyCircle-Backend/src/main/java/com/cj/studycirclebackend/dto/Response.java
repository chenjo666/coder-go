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

    public static Response notContent() {
        return Response.builder().code(204).msg("Not Content").build();
    }

    public static Response badRequest() {
        return Response.builder().code(400).msg("Bad Request").build();
    }

    public static Response notFound() {
        return Response.builder().code(404).msg("Not Found").build();
    }

    /**
     * 登录返回值
     * @return
     */
    public static Response loginSuccess() {
        return Response.builder().code(200).msg("登录成功！").build();
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
    public static Response registerSuccess() {
        return Response.builder().code(200).msg("注册成功！").build();
    }
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


//
//    // 建造者模式构建 Response
//    public static <T> ResponseBuilder<T> builder() {
//        return (ResponseBuilder<T>) new ResponseBuilder<>();
//    }
//    public static class ResponseBuilder<T> {
//        private int code;
//        private String msg;
//        private T data;
//
//        public ResponseBuilder<T> code(int code) {
//            this.code = code;
//            return this;
//        }
//
//        public ResponseBuilder<T> msg(String msg) {
//            this.msg = msg;
//            return this;
//        }
//
//        public ResponseBuilder<T> data(T data) {
//            this.data = data;
//            return this;
//        }
//
//        public ResponseBuilder<T> ok() {
//            this.code = 200;
//            this.msg = "OK";
//            return this;
//        }
//
//        public ResponseBuilder<T> notContent() {
//            this.code = 204;
//            this.msg = "Not Content";
//            return this;
//        }
//        public ResponseBuilder<T> badRequest() {
//            this.code = 400;
//            this.msg = "Bad Request";
//            return this;
//        }
//
//
//        public ResponseBuilder<T> notFound() {
//            this.code = 404;
//            this.msg = "Not Found";
//            return this;
//        }
//
//        public ResponseBuilder<T> loginSuccess() {
//            this.code = 200;
//            this.msg = "登录成功！";
//            return this;
//        }
//        public ResponseBuilder<T> loginFailed() {
//            this.code = 400;
//            this.msg = "邮箱或密码错误！";
//            return this;
//        }
//        public ResponseBuilder<T> registerSuccess() {
//            this.code = 200;
//            this.msg = "注册成功！";
//            return this;
//        }
//        public ResponseBuilder<T> registerFailed() {
//            this.code = 400;
//            this.msg = "注册失败，请输入正确的信息！";
//            return this;
//        }
//        public ResponseBuilder<T> activateSuccess() {
//            this.code = 200;
//            this.msg = "激活成功，请查看邮箱！";
//            return this;
//        }
//        public ResponseBuilder<T> activateFailed() {
//            this.code = 400;
//            this.msg = "激活失败，邮箱已经激活！";
//            return this;
//        }
//
//        public Response<T> build() {
//            Response<T> response = new Response<>();
//            response.code = this.code;
//            response.msg = this.msg;
//            response.data = this.data;
//            return response;
//        }
//    }
}
