package com.example.studycirclebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.studycirclebackend.dao")
public class StudyCircleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyCircleBackendApplication.class, args);
    }

}
