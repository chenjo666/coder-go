package com.cj.codergobackend;

import com.cj.codergobackend.util.EmailUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class EmailUtilTests {
    @Resource
    private EmailUtil emailUtil;
    @Test
    public void testEmail() {
        emailUtil.send("2357808792@qq.com", "hello", "激活码");
    }
}
