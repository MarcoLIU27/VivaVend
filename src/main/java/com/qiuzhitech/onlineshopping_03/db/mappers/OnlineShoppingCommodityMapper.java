package com.qiuzhitech.onlineshopping_03.db.mappers;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;

import java.util.List;
import java.util.Map;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);

    int deductStock(long commodityId);

    void deductStockSP(Map<String, Object> temp);

    List<OnlineShoppingCommodity> listCommodities();

    void revertStock(long commodityId);

    void confirmDeduct(long commodityId);

    List<OnlineShoppingCommodity> searchCommodityByKeyword(String searchKey);
}