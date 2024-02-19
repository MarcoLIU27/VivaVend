package com.qiuzhitech.onlineshopping_03.db.dao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;

public interface OnlineShoppingUserDao {
    int deleteUserById(Long userId);
    int insertUser(OnlineShoppingUser user);
    OnlineShoppingUser queryUserById(Long userId);
    OnlineShoppingUser queryUserByEmail(String email);
    int updateUser(OnlineShoppingUser user);
}
