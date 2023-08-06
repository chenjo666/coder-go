package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.vo.UserVO;
import com.cj.codergobackend.dto.Response;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;

public interface UserService extends IService<User> {
    Response login(String email, String password, HttpServletResponse response);

    Response register(String email, String password, String code, HttpServletResponse response);

    Response activate(String email);

    Response logout(String token);

    UserVO getUserVO(User user);

    List<UserVO> getUsersBySet(Set<Object> userIds);
}
