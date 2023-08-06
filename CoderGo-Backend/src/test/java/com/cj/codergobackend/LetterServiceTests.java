package com.cj.codergobackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.codergobackend.dao.LetterMapper;
import com.cj.codergobackend.pojo.Letter;
import com.cj.codergobackend.service.LetterService;
import com.cj.codergobackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class LetterServiceTests {
    @Resource
    private LetterService letterService;

    @Resource
    private UserService userService;

    @Resource
    private LetterMapper letterMapper;

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
    public void test2() {
        List<Long> list = letterMapper.getToUserIds(1677640320653533186L);
        list.forEach(System.out::println);
    }

//    @Test
//    public void insert() {
//        List<User> list = userService.list();
//        Random random = new Random();
//        List<Letter> letters = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Letter letter = new Letter();
//            letter.setIsRead(0);
//            letter.setUserFromId(list.get(random.nextInt(list.size())).getId());
//            letter.setUserToId(1677640320653533186L);
//            letter.setContent("hello");
//            // 随机生成时间
//            LocalDateTime now = LocalDateTime.now();
//            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
//                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
//            letter.setSendTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
//            letters.add(letter);
//        }
//        letterService.saveBatch(letters);
//    }
}
