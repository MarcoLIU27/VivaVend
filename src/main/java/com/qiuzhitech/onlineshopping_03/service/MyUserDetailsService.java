package com.qiuzhitech.onlineshopping_03.service;

import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    OnlineShoppingUserDao onlineShoppingUserDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 从数据库尝试读取该用户
        OnlineShoppingUser user = onlineShoppingUserDao.queryUserByEmail(email);

        // 用户不存在，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 将数据库形式的roles解析为UserDetails的权限集
        // AuthorityUtils.commaSeparatedStringToAuthorityList是Spring Securit 提供的用于将逗号隔开的权限集字符串切割成可用权限对象列表的方法
        //user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
        // 将用户实体和权限集合都放到UserDetail中，

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles());
        return new OnlineShoppingUserDetail(user, true, authorities);
    }

}