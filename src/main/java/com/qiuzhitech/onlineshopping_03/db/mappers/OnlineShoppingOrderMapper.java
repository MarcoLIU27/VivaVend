package com.qiuzhitech.onlineshopping_03.db.mappers;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;

import java.util.List;

public interface OnlineShoppingOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OnlineShoppingOrder record);

    int insertSharding(OnlineShoppingOrder record);

    int insertSelective(OnlineShoppingOrder record);

    OnlineShoppingOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(OnlineShoppingOrder record);

    int updateByPrimaryKey(OnlineShoppingOrder record);

    OnlineShoppingOrder queryOrderByOrderNo(String orderNo);

    OnlineShoppingOrder selectShardingOrderByPrimaryKey(Long orderId);

    List<OnlineShoppingOrder> queryOrderByUserId(Long userId);
}