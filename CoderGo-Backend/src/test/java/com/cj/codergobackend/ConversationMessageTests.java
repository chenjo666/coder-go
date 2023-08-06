package com.cj.codergobackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.codergobackend.dao.ConversationMessageMapper;
import com.cj.codergobackend.pojo.ConversationMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class ConversationMessageTests {
    @Resource
    private ConversationMessageMapper conversationMessageMapper;
    @Test
    public void setRole() {
        List<ConversationMessage> conversationMessages = conversationMessageMapper.selectList(new QueryWrapper<ConversationMessage>());
        for (int i = 1; i < conversationMessages.size(); i+=2) {
            conversationMessages.get(i).setRole("assistant");
            conversationMessageMapper.updateById(conversationMessages.get(i));
        }

    }
}
