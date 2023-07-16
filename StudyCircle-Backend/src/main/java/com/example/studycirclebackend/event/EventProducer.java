package com.example.studycirclebackend.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.studycirclebackend.dto.Event;
import com.example.studycirclebackend.dto.MailEvent;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void createEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

    public void sendMailEvent(MailEvent mailEvent) {
        kafkaTemplate.send(mailEvent.getTopic(), JSONObject.toJSONString(mailEvent));
    }
}
