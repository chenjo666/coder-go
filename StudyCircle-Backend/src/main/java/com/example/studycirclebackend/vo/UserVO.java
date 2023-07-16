package com.example.studycirclebackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVO {
    private Long userId;
    private String userName;
    private String userAvatar;
}
