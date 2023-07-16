package com.example.studycirclebackend.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AIMessage implements Serializable {
    private String role;
    private String content;
}
