package com.example.cj.studycirclebackend;

import com.example.studycirclebackend.StudyCircleBackendApplication;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//@SpringBootTest(classes = {StudyCircleBackendApplication.class})
//public class KafkaTests {
//    @Autowired
//    private KafkaProducer kafkaProducer;
//    @Test
//    public void test() {
//        kafkaProducer.sendMsg("test", "hello");
//        kafkaProducer.sendMsg("test", "在吗");
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
//
