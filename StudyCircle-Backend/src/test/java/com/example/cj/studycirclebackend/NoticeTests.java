package com.example.cj.studycirclebackend;

import com.example.studycirclebackend.StudyCircleBackendApplication;
import com.example.studycirclebackend.pojo.Notice;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.NoticeService;
import com.example.studycirclebackend.service.PostService;
import com.example.studycirclebackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class NoticeTests {
    @Resource
    NoticeService noticeService;
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    @Test
    public void insert() {
        List<Notice> list = new ArrayList<>();
        Random random = new Random();
        List<User> users = userService.list();
        List<Post> posts = postService.list();
        for (int i = 0; i < 30; i++){
            Notice notice = new Notice();
            notice.setUserFromId(users.get(random.nextInt(users.size())).getId());
            notice.setUserToId(1677640320653533186L);
            int noticeType = random.nextInt(6) + 1;
            notice.setNoticeType(noticeType);
            if (noticeType != 6) {
                notice.setPostId(posts.get(random.nextInt(posts.size())).getId());
            }
            // 随机生成时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            notice.setNoticeTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
            notice.setIsRead(random.nextInt(2));
            list.add(notice);
        }
        noticeService.saveBatch(list);
    }
}
