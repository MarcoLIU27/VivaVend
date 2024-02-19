package com.qiuzhitech.onlineshopping_03.db.mappers;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class OnlineShoppingCommodityMapperTest {

    @Resource
    OnlineShoppingCommodityMapper commodityMapper;

    @Test
    void insert() {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(11L)
                .commodityName("name")
                .commodityDesc("des")
                .price(111)
                .availableStock(10)
                .creatorUserId(1L)
                .lockStock(0)
                .totalStock(10)
                .build();

        commodityMapper.insert(commodity);
    }

    @Test
    void select() {
        OnlineShoppingCommodity onlineShoppingCommodity = commodityMapper.selectByPrimaryKey(11L);
        log.info(onlineShoppingCommodity.toString());

    }

}