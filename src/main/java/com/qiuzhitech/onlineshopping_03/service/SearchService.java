package com.qiuzhitech.onlineshopping_03.service;

import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class SearchService {

    @Resource
    ElasticsearchService esService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    public void addCommodityToEs(OnlineShoppingCommodity commodity) throws IOException {
        esService.addCommodityToEs(commodity);
    }

    public List<OnlineShoppingCommodity> searchCommoditiesES(String keyword, int from, int size) throws IOException {
        return esService.searchCommodities(keyword, from, size);
    }

    public List<OnlineShoppingCommodity> searchCommoditiesSQL(String keyword, int from, int size) {
        return commodityDao.searchCommodityByKeyword(keyword);
    }
}
