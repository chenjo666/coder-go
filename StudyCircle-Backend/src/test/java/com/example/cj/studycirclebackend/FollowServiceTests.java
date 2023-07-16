package com.example.cj.studycirclebackend;

import com.example.studycirclebackend.StudyCircleBackendApplication;
import com.example.studycirclebackend.pojo.Follow;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.FollowService;
import com.example.studycirclebackend.service.PostService;
import com.example.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import net.bytebuddy.matcher.FilterableList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class FollowServiceTests {
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    @Resource
    FollowService followService;

}
