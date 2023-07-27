package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.vo.UserVO;
import com.cj.studycirclebackend.dto.Response;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {
    Response login(String email, String password, HttpServletResponse response);

    Response register(String email, String password, String code, HttpServletResponse response);

    Response activate(String email);

    Response logout(String token);

    UserVO getUserVO(User user);


    Response getUserFollowings(Long userId);
    Response getUserFollowers(Long userId);

}
