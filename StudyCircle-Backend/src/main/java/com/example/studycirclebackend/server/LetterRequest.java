package com.example.studycirclebackend.server;

import lombok.Data;

@Data
public class LetterRequest {
    private Long userFromId;
    private Long userToId;
    private String content;
    private String time;
}
