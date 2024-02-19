package com.qiuzhitech.onlineshopping_03.service.MQ;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_03.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
@RocketMQMessageListener(topic = "CheckOrder",consumerGroup = "CheckOrderGroup")
public class CheckOrderListener implements RocketMQListener<MessageExt> {
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info("Receive message for CheckOrder Topic:" + message);
        OnlineShoppingOrder orderMessage = JSON.parseObject(message, OnlineShoppingOrder.class);
        String orderNum = orderMessage.getOrderNo();
        long commodityId = orderMessage.getCommodityId();
        OnlineShoppingOrder orderDB = orderDao.queryOrderByOrderNo(orderNum);
        if (orderDB == null) {
            log.error("Can't find order in DB"); return;
        }
        int orderStatus = orderDB.getOrderStatus();
        // 0. Invalid order, Since no available stock
        // 1. already create order, pending for payment
        // 2. finished payment
        // 99. invalid order due to payment proceed overtime
        if(orderStatus != 2){
            log.info("Fail to pay order in time, order NO: " + orderDB.getOrderNo());
            //Update DB, revert stock in DB
            orderDB.setOrderStatus(99);
            orderDao.updateOrder(orderDB);
            commodityDao.revertStock(commodityId);
            //Update Redis, revert stock
            String redisKey = "commodity:" + commodityId;
            redisService.revertStock(redisKey);
        }
        else {
            log.info("Skip operation for order:" + JSON.toJSON(orderDB));
        }
    }
}
