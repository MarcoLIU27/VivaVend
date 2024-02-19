package com.qiuzhitech.onlineshopping_03.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController2 {

    @Resource
    DependencyA dependencyA;

    public HelloController2(DependencyA dependencyA){
        this.dependencyA = dependencyA;
    }

    @GetMapping("/hello2")
    public String hello2(){
        return dependencyA.send("hello2");
    }

    @GetMapping("/echo2/{abc}")
    public String echo2(@PathVariable("abc") String abc){
        return "Your input:" + abc;
    }

}
