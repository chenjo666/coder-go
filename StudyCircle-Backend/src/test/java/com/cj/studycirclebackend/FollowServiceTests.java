package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.service.FollowService;
import com.cj.studycirclebackend.service.PostService;
import com.cj.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class FollowServiceTests {
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    @Resource
    FollowService followService;

}
