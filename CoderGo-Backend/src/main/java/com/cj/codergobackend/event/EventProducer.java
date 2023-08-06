package com.cj.codergobackend.event;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void createEvent(Event event) {
        kafkaTemplate.send(event.getNoticeTopic(), JSONObject.toJSONString(event));
//        kafkaTemplate.send(event.getNoticeTopic(), event);
    }
}
