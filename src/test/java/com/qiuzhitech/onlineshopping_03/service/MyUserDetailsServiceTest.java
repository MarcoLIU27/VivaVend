package com.qiuzhitech.onlineshopping_03.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MyUserDetailsServiceTest {
    @Resource
    MyUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("marco@gmail.com");
        log.info(userDetails.toString());
    }
}