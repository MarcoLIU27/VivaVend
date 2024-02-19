package com.qiuzhitech.onlineshopping_03.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CDNPageServiceTest {
    @Resource
    CDNPageService cdnPageService;
    @Test
    void createHtml() throws FileNotFoundException {
        cdnPageService.createHtml(123L);
    }
}