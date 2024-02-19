package com.qiuzhitech.onlineshopping_03.service;

import com.qiuzhitech.onlineshopping_03.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Service
public class CDNPageService {
    @Resource
    TemplateEngine engine;
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    public void createHtml(long commodityId) throws FileNotFoundException {
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
        Map<String, Object> map = new HashMap<>();
        map.put("commodity", commodity);

        Context context = new Context();
        context.setVariables(map);
        File file = new File("src/main/resources/templates/item_detail_" + commodityId + ".html");
        PrintWriter writer = new PrintWriter(file);
        engine.process("item_detail", context, writer);

        writer.close();

    }

}
