package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;

    // 登录业务
    @PostMapping("/login")
    public Response login(String email, String password, HttpServletResponse response) {
        return userService.login(email, password, response);
    }
    // 登出业务
    @DeleteMapping("/logout")
    public Response logout(@CookieValue("token") String token) {
        return userService.logout(token);
    }

    // 激活业务
    @PostMapping("/activation")
    public Response activation(String email) {
        return userService.activate(email);
    }

    // 注册业务
    @PostMapping("/register")
    public Response register(String email, String password, String code, HttpServletResponse response) {
        return userService.register(email, password, code, response);
    }





}
