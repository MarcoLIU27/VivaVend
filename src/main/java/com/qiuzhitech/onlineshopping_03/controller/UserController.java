package com.qiuzhitech.onlineshopping_03.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUserDetail;
import com.qiuzhitech.onlineshopping_03.model.User;
import com.qiuzhitech.onlineshopping_03.service.MyUserDetailsService;
import com.qiuzhitech.onlineshopping_03.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class UserController {

    public Map<String, User> users = new HashMap<>();
    @Resource(name = "lisiUser")
    User nobodyUser;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    OnlineShoppingUserDao onlineShoppingUserDao;

    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    AuthenticationManager authenticationManager;

    @Resource
    MyUserDetailsService userDetailsService;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    @PostMapping("/users")
    public String addUser(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          Map<String, Object> userMap){

        User newUser = new User(name, email);
        userMap.put("a", newUser);
        users.put(name, newUser);

        return "user_detail";
    }

    @GetMapping("/users")
    public String getUser(@RequestParam("name") String name, Map<String, Object> userMap){

        User user = users.getOrDefault(name, nobodyUser);
        userMap.put("a", user);

        return "user_detail";
    }

    @PutMapping("/users")
    public String updateUser(@RequestParam("name") String name,
                             @RequestParam("newEmail") String newEmail,
                             Map<String, Object> userMap){
        User user = users.get(name);
        user.email = newEmail;
        users.put(name, user);

        userMap.put("a", user);
        return "user_detail";
    }

    @GetMapping("/testLogin")
    public String login(){

        return "test_login";
    }

//    @PostMapping("/login")
//    public String createAuthenticationToken(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            String token = jwtTokenUtil.generateToken(userDetails);
//            log.info("token:" + token);
//
//            // 可以选择将token添加到session或者作为URL参数传递
//            // redirectAttributes.addFlashAttribute("token", token);
//            return "redirect:/test?token=" + token; // 或者其他方式传递token
//        } catch (Exception e) {
//            return "redirect:/testLogin?error"; // 登录失败重定向到登录页面，并可选地显示错误信息
//        }
//    }

    // 显示注册表单
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "test_register";
    }

    // 处理注册请求
    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("address") String address,
                               @RequestParam("phone") String phone,
                               @RequestParam("roles") String roles,
                               @RequestParam("password") String password,
                               Map<String, Object> userMap) {

        OnlineShoppingUser user = OnlineShoppingUser.builder()
                .name(name)
                .email(email)
                .address(address)
                .phone(phone)
                .roles(roles)
                .password(passwordEncoder.encode(password))
                .build();
        int res = 0;
        try {
            res = onlineShoppingUserDao.insertUser(user);
            log.info("Try to add new user, result:" + res);
            OnlineShoppingUser userDB = onlineShoppingUserDao.queryUserByEmail(email);
            userMap.put("user", userDB);
            return "redirect:/testLogin";
        } catch (Exception e) {
            log.info("Try to add new user, result:" + res);
            return "redirect:/register";
        }
    }

    @GetMapping("/test/users/{userId}")
    public String testGetUser(@PathVariable("userId") String userId, Map<String, Object> userMap){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser authUser = userDetails.getUser();
            if(!authUser.getUserId().equals(Long.valueOf(userId)))
                return "redirect:/noAccess";
        }

        OnlineShoppingUser user = onlineShoppingUserDao.queryUserById(Long.valueOf(userId));
        userMap.put("a", user);

        return "test_user_detail";
    }

    @GetMapping("/test/users/orders/{userId}")
    public String testGetUserOrders(@PathVariable("userId") String userId,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                    Map<String, Object> res){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser authUser = userDetails.getUser();
            if(!authUser.getUserId().equals(Long.valueOf(userId)))
                return "redirect:/noAccess";
            res.put("user", authUser);
        }

        Map<OnlineShoppingOrder, OnlineShoppingCommodity> orderResults = new HashMap<>();
        PageInfo<OnlineShoppingOrder> OrderPageInfo = orderDao.queryOrderByUserIdAndPage(Long.valueOf(userId),page, size);
        for(OnlineShoppingOrder order : OrderPageInfo.getList()){
            Long commodityId = order.getCommodityId();
            OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
            orderResults.put(order, commodity);
        }
        res.put("ordersMap", orderResults);
        res.put("pageInfo", OrderPageInfo);
        log.info(OrderPageInfo.toString());
        log.info("Total Pages:" + OrderPageInfo.isIsFirstPage());

        return "test_user_orders";
    }

    @GetMapping("/noAccess")
    public String showNoAccessPage(){

        return "no_access";
    }


}