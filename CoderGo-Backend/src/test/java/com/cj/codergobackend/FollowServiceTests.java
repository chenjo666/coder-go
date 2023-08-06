package com.cj.codergobackend;

import com.cj.codergobackend.service.FollowService;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class FollowServiceTests {
    @Resource
    UserService userService;
    @Resource
    ArticleService articleService;
    @Resource
    FollowService followService;

}
