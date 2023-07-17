package com.example.studycirclebackend.websocket;

import lombok.Data;

@Data
public class LetterRequest {
    private Long userFromId;
    private Long userToId;
    private String content;
    private String time;
}
