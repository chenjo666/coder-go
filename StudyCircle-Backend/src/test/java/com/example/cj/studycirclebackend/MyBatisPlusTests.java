package com.example.cj.studycirclebackend;

import com.example.studycirclebackend.StudyCircleBackendApplication;
import com.example.studycirclebackend.dao.UserMapper;
import com.example.studycirclebackend.pojo.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class MyBatisPlusTests {
    @Resource
    private UserMapper userMapper;
    @Test
    public void testUse() {
        List<User> users = userMapper.selectList(null);
        for (User u : users) {
            System.out.println(u);
        }
    }
}
