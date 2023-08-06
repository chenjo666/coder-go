package com.cj.codergobackend;

import com.cj.codergobackend.dao.UserMapper;
import com.cj.codergobackend.pojo.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
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
