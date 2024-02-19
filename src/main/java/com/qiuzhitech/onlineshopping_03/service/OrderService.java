package com.qiuzhitech.onlineshopping_03.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_03.service.MQ.RocketMQService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    @Resource
    OnlineShoppingOrderDao orderDao;
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @Resource
    RocketMQService rocketMQService;

    public OnlineShoppingOrder processOrder(long commodityId, long userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
        // Check
        int availableStock = commodityDetail.getAvailableStock();

        if (availableStock > 0) {
            availableStock -= 1;
            log.info("Process succesful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .userId(userId)
                    .commodityId(commodityId)
                    .orderNo(UUID.randomUUID().toString())
                    .orderAmount(commodityDetail.getPrice().longValue())
                    .createTime(new Date())
                    // create order
                    // 0. Invalid order, Since no available stock
                    // 1. already create order, pending for payment
                    // 2. finished payment
                    // 99. invalid order due to payment proceed overtime
                    .orderStatus(1)
                    .build();
            orderDao.insertOrder(order);
            commodityDetail.setAvailableStock(availableStock);
            commodityDao.updateCommodity(commodityDetail);
            return order;
        } else {
            log.info("Process order failed due to no available stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder getOrderByOrderNo(String orderNum) {
        return orderDao.queryOrderByOrderNo(orderNum);
    }

    public int payOrder(String orderNum) {
        OnlineShoppingOrder order = getOrderByOrderNo(orderNum);
        order.setOrderStatus(2);
        order.setPayTime(new Date());
        long commodityId = order.getCommodityId();
        commodityDao.confirmDeduct(commodityId);
        return orderDao.updateOrder(order);
    }

    public OnlineShoppingOrder processOrderOneSQL(long commodityId, long userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
        // Check
        int availableStock = commodityDetail.getAvailableStock();

        if (availableStock > 0) {
            int result = commodityDao.deductStock(commodityId);
            if (result > 0) {
                log.info("Process succesful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderSP(long commodityId, long userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
        // Check
        int availableStock = commodityDetail.getAvailableStock();
        if (availableStock > 0) {
            int result = commodityDao.deductStockSP(commodityId);
            if (result > 0) {

                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                log.info("Process succesful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderRedis(long commodityId, long userId) {
        String redisKey = "commodity:" + commodityId;
        // Check
        long availableStock = redisService.stockDeduct(redisKey);
        if (availableStock >= 0) {
            int result = commodityDao.deductStock(commodityId);
            if (result > 0) {
                OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
                log.info("Process succesful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderDistribuedLock(long commodityId, long userId) {
        String requestId = UUID.randomUUID().toString();
        String redisKey = "lockCommodity:" + commodityId;
        boolean res = redisService.tryDistribuedLock(redisKey, requestId, 5000);
        if(res){
            // Check
            int result = commodityDao.deductStock(commodityId);
            if (result > 0) {
                OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
                log.info("Process succesful for commodityId:" + commodityId);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                redisService.releaseDistribuedLock(redisKey, requestId);
                return order;
            }
        }
        return null;
    }

    public OnlineShoppingOrder processOrderFinal(long commodityId, long userId) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        String redisKey = "commodity:" + commodityId;
        // Check
        long availableStock = redisService.stockDeduct(redisKey);
        if (availableStock >= 0) {
            OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(commodityId);
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .build();
            log.info("Create message for commodityId:" + commodityId);
            rocketMQService.sendMessage("CreateOrder", JSON.toJSONString(order));

            return order;
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }


}