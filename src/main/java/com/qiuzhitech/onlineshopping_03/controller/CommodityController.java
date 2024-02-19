package com.qiuzhitech.onlineshopping_03.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUser;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingUserDetail;
import com.qiuzhitech.onlineshopping_03.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class CommodityController {

    @Resource
    OnlineShoppingCommodityDao commodityDao;
    @Resource
    SearchService searchService;
    @Resource
    OnlineShoppingUserDao onlineShoppingUserDao;

    @GetMapping("/")
    public String listItems(Map<String, Object>resultMap) {
        List<OnlineShoppingCommodity> commodityList = commodityDao.listCommodities();
        resultMap.put("itemList", commodityList);
        return "list_items";
    }

    @GetMapping("/header")
    public String header(Map<String, Object>resultMap) {
        return "header";
    }

    @GetMapping("/loggedInHeader/{userId}")
    public String loggedInHeader(@PathVariable("userId") String userId, Map<String, Object>resultMap) {
        OnlineShoppingUser user = onlineShoppingUserDao.queryUserById(Long.valueOf(userId));
        resultMap.put("user", user);
        return "logged_in_header";
    }



    @GetMapping("/test")
    public String testIndex(Map<String, Object>resultMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser user = userDetails.getUser();
            resultMap.put("user", user);
        }

        List<OnlineShoppingCommodity> commodityList = commodityDao.listCommodities();
        resultMap.put("itemList", commodityList);

        return "test_index";
    }

    @PostMapping("/testSearchAction")
    public String testSearchItems(@RequestParam("keyWord") String keyword,
                              Map<String, Object> res) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser user = userDetails.getUser();
            res.put("user", user);
        }
        List<OnlineShoppingCommodity> searchResults = searchService.searchCommoditiesES(keyword, 0, 10);
        res.put("itemList", searchResults);
        return "test_search_items";
    }

    @GetMapping("/test/item/{commodityId}")
    public String testGetItem(@PathVariable("commodityId") String commodityId,
                          Map<String, Object> res) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            OnlineShoppingUserDetail userDetails = (OnlineShoppingUserDetail) authentication.getPrincipal();
            OnlineShoppingUser user = userDetails.getUser();
            res.put("user", user);
            res.put("anth", 1);
        }
        else{
            res.put("anth", 0);
        }
        OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(Long.parseLong(commodityId));
        res.put("commodity", commodityDetail);
        return "test_item_detail";
    }

    @RequestMapping("/addItem")
    public String addItem() {
        return "add_commodity";
    }

    @PostMapping("/addItemAction")
    public String addItemAction(@RequestParam("commodityId") long commodityId,
                                @RequestParam("commodityName") String commodityName,
                                @RequestParam("commodityDesc") String commodityDesc,
                                @RequestParam("price") int price,
                                @RequestParam("availableStock") int availableStock,
                                @RequestParam("creatorUserId") long creatorUserId,
                                Map<String, Object> resultMap) throws IOException {

        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .totalStock(availableStock)
                .lockStock(0)
                .creatorUserId(creatorUserId)
                .build();

        resultMap.put("Item", commodity);
        commodityDao.insertCommodity(commodity);
        searchService.addCommodityToEs(commodity);

        return "add_commodity_success";
    }

    @GetMapping("/listItems/{sellerId}")
    public String listItemsByUserId(@PathVariable("sellerId") String sellerId, Map<String, Object>resultMap) {
        List<OnlineShoppingCommodity> commodityList = commodityDao.listCommoditiesByUserId(Long.parseLong(sellerId));
        resultMap.put("itemList", commodityList);
        return "list_items";
    }

    @GetMapping("/item/{commodityId}")
    public String getItem(@PathVariable("commodityId") String commodityId,
                          Map<String, Object> res) {

        try(Entry entry = SphU.entry("commodityDetailRule", EntryType.IN, 1, commodityId)) {
            OnlineShoppingCommodity commodityDetail = commodityDao.queryCommodityById(Long.parseLong(commodityId));
            res.put("commodity", commodityDetail);
            return "item_detail";
        } catch (BlockException e) {
            return "wait";
        }
    }

    @PostMapping("/searchAction")
    public String searchItems(@RequestParam("keyWord") String keyword,
                             Map<String, Object> res) throws IOException {
        List<OnlineShoppingCommodity> searchResults = searchService.searchCommoditiesES(keyword, 0, 10);
        res.put("itemList", searchResults);
        return "search_items";
    }

    @PostConstruct
    public void CommodityControllerFlow(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();

        rule.setResource("commodityDetailRule");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

}