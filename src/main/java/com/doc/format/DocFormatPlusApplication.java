package com.doc.format;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.doc.format.mapper")
@EnableAsync
@EnableScheduling
public class DocFormatPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocFormatPlusApplication.class, args);
    }

}
