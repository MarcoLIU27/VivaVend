package com.qiuzhitech.onlineshopping_03.db.dao;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OnlineShoppingCommodityDaoTest {

    @Resource
    OnlineShoppingCommodityDao dao;
    @Test
    void insertCommodity() {
        OnlineShoppingCommodity onlineShoppingCommodity = OnlineShoppingCommodity.builder()
                .commodityId(123L)
                .price(123)
                .commodityDesc("desc")
                .commodityName("name")
                .creatorUserId(1L)
                .availableStock(111)
                .totalStock(10)
                .lockStock(0)
                .build();
        dao.insertCommodity(onlineShoppingCommodity);
    }

    @Test
    void listCommoditiesByUserId() {
        List<OnlineShoppingCommodity> commodities = dao.listCommoditiesByUserId(123L);
        log.info(commodities.toString());
    }
}