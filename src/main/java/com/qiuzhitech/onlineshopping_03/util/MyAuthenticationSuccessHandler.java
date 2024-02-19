package com.qiuzhitech.onlineshopping_03.util;

import com.qiuzhitech.onlineshopping_03.service.MyUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private MyUserDetailsService userDetailsService; // 确保这里正确注入了你的UserDetailsService

    public MyAuthenticationSuccessHandler() {
        super();
        // 设置重定向目标URL参数名称
        this.setTargetUrlParameter("redirectTo");
        // 设置默认的重定向页面，如果没有访问的页面被保存
        this.setDefaultTargetUrl("/test");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        // 根据认证信息生成JWT
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("token:" + token);
        // 将JWT添加到响应头
        response.addHeader("Authorization", "Bearer " + token);

        // 调用父类的onAuthenticationSuccess以利用SavedRequestAware的特性，进行重定向处理
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

