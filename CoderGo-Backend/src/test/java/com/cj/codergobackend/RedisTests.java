package com.cj.codergobackend;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class RedisTests {
    @Resource
    RedisTemplate redisTemplate;
    @Test
    public void test() {
        String key = "user:1677640320653533186:following";
        Set set = redisTemplate.opsForZSet().reverseRangeByScore(key, -1, Double.MAX_VALUE);
        System.out.println(set);

    }
}
