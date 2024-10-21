package com.doc.format;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.doc.format.mapper")
public class DocFormatPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocFormatPlusApplication.class, args);
    }

}
