package com.qiuzhitech.onlineshopping_03.db.dao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int deleteCommodityById(Long commodityId);
    int insertCommodity(OnlineShoppingCommodity record);
    OnlineShoppingCommodity queryCommodityById(Long commodityId);
    int updateCommodity(OnlineShoppingCommodity record);
    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);
    List<OnlineShoppingCommodity> listCommodities();
    int deductStock(long commodityId);

    int deductStockSP(long commodityId);

    void revertStock(long commodityId);

    void confirmDeduct(long commodityId);

    List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword);
}
