package com.qiuzhitech.onlineshopping_03.db.dao;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OnlineShoppingUserDaoTest {
    @Resource
    OnlineShoppingUserDao userDao;

    @Test
    void insertUser() {
        OnlineShoppingUser user = OnlineShoppingUser.builder()
                .userId(2L)
                .name("zhangsan")
                .address("Seattle")
                .email("z123123@hotmail.com")
                .phone("111111")
                .password("1234")
                .roles("User")
                .build();
        userDao.insertUser(user);
    }

    @Test
    void updateUser() {
        OnlineShoppingUser onlineShoppingUser = userDao.queryUserById(123L);
        onlineShoppingUser.setPhone("2222");
        onlineShoppingUser.setName("张三");
        userDao.updateUser(onlineShoppingUser);

        log.info(userDao.queryUserById(123L).toString());

    }

    @Test
    void queryByEmail(){
        OnlineShoppingUser user = userDao.queryUserByEmail("marco@gmail.com");
        log.info(user.toString());

    }



}