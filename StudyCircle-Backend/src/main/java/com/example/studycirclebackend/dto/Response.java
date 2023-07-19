package com.example.studycirclebackend.dto;

import lombok.Data;

@Data
public class Response<T> {
    private int code; // 响应码
    private String msg; // 响应消息
    private T data; // 数据

    // 建造者模式构建 Response
    public static <T> ResponseBuilder<T> builder() {
        return new ResponseBuilder<>();
    }
    public static class ResponseBuilder<T> {
        private int code;
        private String msg;
        private T data;

        public ResponseBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ResponseBuilder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseBuilder<T> ok() {
            this.code = 200;
            this.msg = "OK";
            return this;
        }

        public ResponseBuilder<T> notContent() {
            this.code = 204;
            this.msg = "Not Content";
            return this;
        }
        public ResponseBuilder<T> badRequest() {
            this.code = 400;
            this.msg = "Bad Request";
            return this;
        }


        public ResponseBuilder<T> notFound() {
            this.code = 404;
            this.msg = "Not Found";
            return this;
        }

        public ResponseBuilder<T> loginSuccess() {
            this.code = 200;
            this.msg = "登录成功！";
            return this;
        }
        public ResponseBuilder<T> loginFailed() {
            this.code = 400;
            this.msg = "邮箱或密码错误！";
            return this;
        }
        public ResponseBuilder<T> registerSuccess() {
            this.code = 200;
            this.msg = "注册成功！";
            return this;
        }
        public ResponseBuilder<T> registerFailed() {
            this.code = 400;
            this.msg = "注册失败，请输入正确的信息！";
            return this;
        }
        public ResponseBuilder<T> activateSuccess() {
            this.code = 200;
            this.msg = "激活成功，请查看邮箱！";
            return this;
        }
        public ResponseBuilder<T> activateFailed() {
            this.code = 400;
            this.msg = "激活失败，邮箱已经激活！";
            return this;
        }

        public Response<T> build() {
            Response<T> response = new Response<>();
            response.code = this.code;
            response.msg = this.msg;
            response.data = this.data;
            return response;
        }
    }
}
