package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Response getResponse() {
        Response response = Response.builder()
                .code(200)
                .msg("成功")
                .data(new HashMap<>())
                .build();
        return response;
    }
}
