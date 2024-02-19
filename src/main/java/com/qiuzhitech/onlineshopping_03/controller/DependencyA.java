package com.qiuzhitech.onlineshopping_03.controller;

import org.springframework.stereotype.Service;

@Service
public class DependencyA {
    
    public static String Lyon = "lyon1";
    public String send(String body) {
        //comment
	    return body;
    }
}
