package com.qiuzhitech.onlineshopping_03.util;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class JwtTokenUtilTest {

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Test
    void generateToken() {
        OnlineShoppingUser user = OnlineShoppingUser.builder()
                .userId(112L)
                .name("zhangsan")
                .address("Seattle")
                .email("z123123@hotmail.com")
                .phone("111111")
                .password("1234")
                .roles("User")
                .build();
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles());
        UserDetails userDetails = new OnlineShoppingUserDetail(user, true, authorities);
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("token:" + token);
    }
}