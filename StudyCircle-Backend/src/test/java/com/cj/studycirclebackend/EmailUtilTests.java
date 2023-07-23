package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.util.EmailUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class EmailUtilTests {
    @Resource
    private EmailUtil emailUtil;
    @Test
    public void testEmail() {
        emailUtil.send("2357808792@qq.com", "hello", "激活码");
    }
}
