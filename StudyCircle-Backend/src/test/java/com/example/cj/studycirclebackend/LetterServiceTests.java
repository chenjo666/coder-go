package com.example.cj.studycirclebackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.studycirclebackend.StudyCircleBackendApplication;
import com.example.studycirclebackend.pojo.Letter;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.LetterService;
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
public class LetterServiceTests {
    @Resource
    private LetterService letterService;

    @Resource
    private UserService userService;

    @Test
    public void test() {
        List<Letter> letters = letterService.list(new QueryWrapper<Letter>()
                .select("user_from_id")
                .eq("user_to_id", 1677640320653533186L)
                .groupBy("user_from_id"));
        for (Letter letter : letters) {
            System.out.println(letter);
        }
    }

    @Test
    public void insert() {
        List<User> list = userService.list();
        Random random = new Random();
        List<Letter> letters = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Letter letter = new Letter();
            letter.setIsRead(0);
            letter.setUserFromId(list.get(random.nextInt(list.size())).getId());
            letter.setUserToId(1677640320653533186L);
            letter.setContent("hello");
            // 随机生成时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            letter.setSendTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
            letters.add(letter);
        }
        letterService.saveBatch(letters);
    }
}
