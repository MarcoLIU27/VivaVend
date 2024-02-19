package com.qiuzhitech.onlineshopping_03.controller;

import com.qiuzhitech.onlineshopping_03.db.po.OnlineShoppingCommodity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class HelloController {

    @Resource
    DependencyA dependencyA;


    public HelloController(DependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }
    @GetMapping("/hello")
    public String hello() {
        return dependencyA.send("Hello world!!!");
    }

    @GetMapping("/echo/{text}")
    public String echo(@PathVariable("text") String abc) {
        return "You just input: " + abc;
    }

}
