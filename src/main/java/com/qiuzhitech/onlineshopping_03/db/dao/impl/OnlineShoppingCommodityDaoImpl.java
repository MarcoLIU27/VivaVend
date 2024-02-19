package com.qiuzhitech.onlineshopping_03.db.dao.impl;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao{
    @Resource
    OnlineShoppingCommodityMapper mapper;

    @Override
    public int deleteCommodityById(Long commodityId) {
        return mapper.deleteByPrimaryKey(commodityId);
    }

    @Override
    public int insertCommodity(OnlineShoppingCommodity record) {
        return mapper.insert(record);
    }

    @Override
    public OnlineShoppingCommodity queryCommodityById(Long commodityId) {
        return mapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId) {
        return mapper.listCommoditiesByUserId(userId);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return mapper.listCommodities();
    }

    @Override
    public int deductStock(long commodityId) {
        return mapper.deductStock(commodityId);
    }

    @Override
    public int deductStockSP(long commodityId) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("commodityId", commodityId);
        temp.put("res", 0);
        mapper.deductStockSP(temp);
        Object res = temp.getOrDefault("res", 0);
        return (int) res;
    }

    @Override
    public void revertStock(long commodityId) {
        mapper.revertStock(commodityId);
    }

    @Override
    public void confirmDeduct(long commodityId) {
        mapper.confirmDeduct(commodityId);
    }

    @Override
    public List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword) {
        String searchKey = "%" + keyword + "%";
        return mapper.searchCommodityByKeyword(searchKey);
    }

}
