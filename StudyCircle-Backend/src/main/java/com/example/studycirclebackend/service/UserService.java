package com.example.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {
    Response login(String email, String password, HttpServletResponse response);

    Response register(String email, String password, String code, HttpServletResponse response);

    Response activate(String email);

    Response logout(String token);

    UserVO getUserVO(User user);


    Response followUser(Long targetUserId);
    Response unFollowUser(Long targetUserId);
    Response getUserFollowings(Long userId);
    Response getUserFollowers(Long userId);

}
