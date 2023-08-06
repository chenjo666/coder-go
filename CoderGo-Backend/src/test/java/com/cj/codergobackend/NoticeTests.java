package com.cj.codergobackend;

import com.cj.codergobackend.pojo.Notice;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.NoticeService;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class NoticeTests {
    @Resource
    NoticeService noticeService;
    @Resource
    UserService userService;
    @Resource
    ArticleService articleService;
    @Test
    public void insert() {
        List<Notice> list = new ArrayList<>();
        Random random = new Random();
        List<User> users = userService.list();
        List<Article> articles = articleService.list();
        for (int i = 0; i < 30; i++){
            Notice notice = new Notice();
            notice.setUserFromId(users.get(random.nextInt(users.size())).getId());
            notice.setUserToId(1677640320653533186L);
            int noticeType = random.nextInt(6) + 1;
            notice.setNoticeType(noticeType);
            if (noticeType != 6) {
                notice.setArticleId(articles.get(random.nextInt(articles.size())).getId());
            }
            // 随机生成时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            notice.setCreatedAt(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
            notice.setIsRead(random.nextInt(2));
            list.add(notice);
        }
        noticeService.saveBatch(list);
    }
}
