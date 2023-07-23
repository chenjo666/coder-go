package com.cj.studycirclebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cj.studycirclebackend.dao")
public class StudyCircleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyCircleBackendApplication.class, args);
    }

}
