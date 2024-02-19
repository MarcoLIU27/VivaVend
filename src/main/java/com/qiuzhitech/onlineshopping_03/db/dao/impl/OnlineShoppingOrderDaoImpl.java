package com.qiuzhitech.onlineshopping_03.db.dao.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {
    @Resource
    OnlineShoppingOrderMapper mapper;

    @Override
    public int deleteOrderById(Long orderId) {
        return mapper.deleteByPrimaryKey(orderId);
    }

    @Override
    public int insertOrder(OnlineShoppingOrder record) {
        return mapper.insert(record);
    }

    @Override
    public int insertShardingOrder(OnlineShoppingOrder record) {
        return mapper.insertSharding(record);
    }

    @Override
    public OnlineShoppingOrder queryOrderById(Long orderId) {
        return mapper.selectByPrimaryKey(orderId);
    }

    @Override
    public OnlineShoppingOrder queryShardingOrderById(Long orderId) {
        return mapper.selectShardingOrderByPrimaryKey(orderId);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNo(String orderNo) {
        return mapper.queryOrderByOrderNo(orderNo);
    }

    @Override
    public List<OnlineShoppingOrder> queryOrderByUserId(Long userId) {
        return mapper.queryOrderByUserId(userId);
    }

    @Override
    public PageInfo<OnlineShoppingOrder> queryOrderByUserIdAndPage(Long userId, int page, int size) {
        PageHelper.startPage(page, size);
        List<OnlineShoppingOrder> orders = mapper.queryOrderByUserId(userId);

        return new PageInfo<>(orders);
    }
}
