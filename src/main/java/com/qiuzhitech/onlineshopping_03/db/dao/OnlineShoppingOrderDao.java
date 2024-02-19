package com.qiuzhitech.onlineshopping_03.db.dao;
import com.github.pagehelper.PageInfo;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;

import java.util.List;

public interface OnlineShoppingOrderDao {
    int deleteOrderById(Long orderId);
    int insertOrder(OnlineShoppingOrder record);
    int insertShardingOrder(OnlineShoppingOrder record);
    OnlineShoppingOrder queryOrderById(Long orderId);

    OnlineShoppingOrder queryShardingOrderById(Long orderId);
    int updateOrder(OnlineShoppingOrder record);
    OnlineShoppingOrder queryOrderByOrderNo(String orderNo);

    List<OnlineShoppingOrder> queryOrderByUserId(Long userId);

    PageInfo<OnlineShoppingOrder> queryOrderByUserIdAndPage(Long userId, int page, int size);
}
