package com.qiuzhitech.onlineshopping_03.db.mappers;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;

public interface OnlineShoppingUserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(OnlineShoppingUser record);

    int insertSelective(OnlineShoppingUser record);

    OnlineShoppingUser selectByPrimaryKey(Long userId);

    OnlineShoppingUser selectByEmail(String email);

    int updateByPrimaryKeySelective(OnlineShoppingUser record);

    int updateByPrimaryKey(OnlineShoppingUser record);

}