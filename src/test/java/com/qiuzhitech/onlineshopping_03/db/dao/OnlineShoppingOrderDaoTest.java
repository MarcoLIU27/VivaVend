package com.qiuzhitech.onlineshopping_03.db.dao;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_03.util.SnowFlakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OnlineShoppingOrderDaoTest {
    @Resource
    OnlineShoppingOrderDao orderDao;

    SnowFlakeIdWorker sfWorker = new SnowFlakeIdWorker(0, 0);

    @Test
    void insertOrder() {
        for (int i = 0; i < 100; i++) {
            long orderId = i + 100L;
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .orderStatus(0)
                    .orderNo("123")
                    .orderId(orderId)
                    .orderAmount(123L)
                    .commodityId(123L)
                    .createTime(new Date())
                    .payTime(new Date())
                    .userId(123L)
                    .orderStatus(0)
                    .build();
            orderDao.insertShardingOrder(order);
        }
    }

    @Test
    void queryShardingOrderById() {
        OnlineShoppingOrder order1 = orderDao.queryShardingOrderById(101L);
        log.info(order1.toString());
        OnlineShoppingOrder order2 = orderDao.queryShardingOrderById(102L);
        log.info(order2.toString());
    }

    @Test
    void insertOrderBySnowFlake() {
        List<OnlineShoppingOrder> orders = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long orderId = sfWorker.nextId();
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .orderStatus(0)
                    .orderNo("123")
                    .orderId(orderId)
                    .orderAmount(123L)
                    .commodityId(123L)
                    .createTime(new Date())
                    .payTime(new Date())
                    .userId(123L)
                    .orderStatus(0)
                    .build();
            orders.add(order);
        }
        for (int i = 0; i < 100; i++) {
            orderDao.insertShardingOrder(orders.get(i));
        }
    }
}