package com.qiuzhitech.onlineshopping_03.components;

import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodities();
        for (OnlineShoppingCommodity commodity: onlineShoppingCommodities) {
            String redisKey = "commodity:" + commodity.getCommodityId();
            long value = commodity.getAvailableStock();
            redisService.setValue(redisKey, value);
            log.info("PreHeat starting, initialize commodity:" + commodity.getCommodityId());
        }
    }
}