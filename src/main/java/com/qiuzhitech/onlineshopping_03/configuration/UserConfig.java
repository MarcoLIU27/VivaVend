package com.qiuzhitech.onlineshopping_03.configuration;

import com.qiuzhitech.onlineshopping_03.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean(name = "nobodyUser")
    public User nobodyUser(){
        return new User("nobody", "nobody_email");
    }

    @Bean(name = "lisiUser")
    public User lisiUser(){
        return new User("lisi", "lisi_email");
    }
}
