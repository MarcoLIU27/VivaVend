package com.qiuzhitech.onlineshopping_03.service.MQ;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
@RocketMQMessageListener(topic = "CreateOrder",consumerGroup = "CreateOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt> {

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    RocketMQService rocketMQService;
    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info("Recieve message for CreateOrder Topic, body: " + message);
        OnlineShoppingOrder order = JSON.parseObject(message, OnlineShoppingOrder.class);
        long commodityId = order.getCommodityId();
        int result = commodityDao.deductStock(commodityId);
        if(result > 0){
            OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
            order.setOrderStatus(1);
            order.setCreateTime(new Date());
            order.setOrderAmount(commodityDetail.getPrice().longValue());
            log.info("Process succesful for commodityId:" + commodityId);
            try {
                rocketMQService.sendDelayMessage("CheckOrder", JSON.toJSONString(order), 9);
            } catch (MQBrokerException | RemotingException | InterruptedException | MQClientException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            // 0. Invalid order, Since no available stock
            order.setOrderStatus(0);
        }
        orderDao.insertOrder(order);
    }
}
