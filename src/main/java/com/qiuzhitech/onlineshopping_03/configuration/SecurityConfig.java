package com.qiuzhitech.onlineshopping_03.configuration;

import com.qiuzhitech.onlineshopping_03.service.MyUserDetailsService;
import com.qiuzhitech.onlineshopping_03.util.JwtAuthenticationTokenFilter;
import com.qiuzhitech.onlineshopping_03.util.JwtTokenUtil;
import com.qiuzhitech.onlineshopping_03.util.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig{

    AuthenticationManager authenticationManager;
    @Resource
    private MyUserDetailsService userDetailsService;
    @Resource
    JwtTokenUtil jwtTokenUtil;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        authenticationManager = authenticationManagerBuilder.build();

        http.authorizeRequests()
                .antMatchers("/test/commodity/**", "/test/users/**")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .authenticationManager(authenticationManager)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                //开启表单认证
                .formLogin()
                .loginPage("/testLogin")
                .permitAll()
                //指登录成功后，是否始终跳转到登录成功url。它默认为false
//                .defaultSuccessUrl("/test",true)
                .successHandler(savedRequestAwareAuthenticationSuccessHandler())
                //post登录接口，登录验证由系统实现
                .loginProcessingUrl("/login")
                //用户密码错误跳转接口
                .failureUrl("/testLogin?error=true")
                //要认证的用户参数名，默认username
                .usernameParameter("username")
                //要认证的密码参数名，默认password
                .passwordParameter("password")
                .and()
                //配置注销
                .logout()
                //注销接口
                .logoutUrl("/logout")
                //注销成功后跳转到的接口
                .logoutSuccessUrl("/test")
                .permitAll()
                //删除自定义的cookie
                .deleteCookies("myCookie")
                .and()
                //注意:需禁用crsf防护功能,否则登录不成功
                .csrf()
                .disable();


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().antMatchers("/img/**", "/css/**", "/js/**", "/main.css");
    }

    @Bean
    public AuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authSuccessHandler.setTargetUrlParameter("redirectTo");
        // 设置默认的重定向页面，如果没有访问的页面被保存
        authSuccessHandler.setDefaultTargetUrl("/test");
        return authSuccessHandler;
    }

    @Bean
    public MyAuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MyAuthenticationSuccessHandler();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT filter
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }
}
