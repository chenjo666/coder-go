package com.cj.studycirclebackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.dao.MessageMapper;
import com.cj.studycirclebackend.pojo.Message;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class MessageTests {
    @Resource
    private MessageMapper messageMapper;
    @Test
    public void setRole() {
        List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>());
        for (int i = 1; i < messages.size();i+=2) {
            messages.get(i).setRole("assistant");
            messageMapper.updateById(messages.get(i));
        }

    }
}
