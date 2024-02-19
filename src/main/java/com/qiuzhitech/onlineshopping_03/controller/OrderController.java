package com.qiuzhitech.onlineshopping_03.controller;

import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUserDetail;
import com.qiuzhitech.onlineshopping_03.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class OrderController {

    @Resource
    OrderService orderService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    OnlineShoppingUserDao userDao;

    @Resource
    OnlineShoppingOrderDao orderDao;
    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(
            @PathVariable("userId") String userId,
            @PathVariable("commodityId") String commodityId,
            Map<String, Object> res) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        //OnlineShoppingOrder order = orderService.processOrder(Long.parseLong(commodityId), Long.parseLong(userId));
        OnlineShoppingOrder order = orderService.processOrderFinal(Long.parseLong(commodityId), Long.parseLong(userId));
        String resultInfo = "";
        if (order != null) {
            resultInfo = "Order created successfully! Order Num:"+ order.getOrderNo();
            res.put("orderNo", order.getOrderNo());
        } else {
            resultInfo = "The commodity is out of stock";
        }
        res.put("resultInfo", resultInfo);

        return "order_result";
    }

    @RequestMapping("/commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> res) {
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
        res.put("order", order);
        res.put("commodity", commodity);
        return "order_check";
    }

    @RequestMapping("/commodity/payOrder/{orderNum}")
    public String payorder(@PathVariable("orderNum") String orderNum,
                           Map<String, Object> res) {
        orderService.payOrder(orderNum);
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
        res.put("order", order);
        res.put("commodity", commodity);
        return "order_check";
    }

    @RequestMapping("/test/commodity/buy/{userId}/{commodityId}")
    public String testBuyCommodity(
            @PathVariable("userId") String userId,
            @PathVariable("commodityId") String commodityId,
            Map<String, Object> res) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {

        OnlineShoppingUser user = userDao.queryUserById(Long.valueOf(userId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser authUser = userDetails.getUser();
            if(!authUser.getUserId().equals(Long.valueOf(userId)))
                return "redirect:/noAccess";
        }
        res.put("user", user);

        OnlineShoppingOrder order = orderService.processOrderFinal(Long.parseLong(commodityId), Long.parseLong(userId));
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(Long.parseLong(commodityId));
        String resultInfo = "";
        if (order != null) {
            resultInfo = "Order created successfully!";
            res.put("commodity", commodity);
            res.put("order", order);
            res.put("success", 1);
            res.put("resultInfo", resultInfo);

            // 重定向到订单查询页面，使用订单号
            return "redirect:/test/commodity/orderQuery/" + order.getOrderNo();

        } else {
            resultInfo = "Sorry! The commodity is out of stock.";
            res.put("commodity", commodity);
            res.put("success", 0);
            res.put("resultInfo", resultInfo);
        }


        return "test_order_result";
    }

    @RequestMapping("/test/commodity/orderQuery/{orderNum}")
    public String testOrderQuery(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> res) {
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);

        if(order != null){
            Long userId = order.getUserId();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
                OnlineShoppingUser authUser = userDetails.getUser();
                if(!authUser.getUserId().equals(userId))
                    return "redirect:/noAccess";
                else{
                    res.put("user", authUser);
                }
            }
            OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
            res.put("order", order);
            res.put("commodity", commodity);
            res.put("success", 1); // Order already created!
        }
        else{
            res.put("success", 0); // Order still processing!
            res.put("refresh", 2);
        }
        return "test_order_check";
    }

    @RequestMapping("/test/commodity/payInfo/{orderNum}")
    public String testPayInfo(@PathVariable("orderNum") String orderNum,
                           Map<String, Object> res) {

        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);

        Long userId = order.getUserId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser authUser = userDetails.getUser();
            if(!authUser.getUserId().equals(userId))
                return "redirect:/noAccess";
            else{
                res.put("user", authUser);
            }
        }
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
        res.put("order", order);
        res.put("commodity", commodity);
        return "test_pay_order";
    }

    @RequestMapping("/test/commodity/payOrder/{orderNum}")
    public String testPayOrder(@PathVariable("orderNum") String orderNum,
                           Map<String, Object> res) {
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);
        Long userId = order.getUserId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser authUser = userDetails.getUser();
            if(!authUser.getUserId().equals(userId))
                return "redirect:/noAccess";
            else{
                res.put("user", authUser);
            }
        }
        if(order.getOrderStatus() == 1){
            int payRes = orderService.payOrder(orderNum);
        }

        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
        res.put("order", order);
        res.put("commodity", commodity);
        // 重定向到订单查询页面，使用订单号
        return "redirect:/test/commodity/orderQuery/" + order.getOrderNo();
    }

}