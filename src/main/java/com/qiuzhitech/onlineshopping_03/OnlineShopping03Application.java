package com.qiuzhitech.onlineshopping_03;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.qiuzhitech.onlineshopping_03.db.mappers")
public class OnlineShopping03Application {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShopping03Application.class, args);
    }

}
