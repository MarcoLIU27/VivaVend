package com.qiuzhitech.onlineshopping_03.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
class HelloController2Test {
    HelloController2 test = new HelloController2(new DependencyA());
    @Mock
    DependencyA mockA;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void hellotest(){
        String ans = test.hello2();
        assertEquals(ans, "hello2");
    }
    @Test
    void mockHellotest(){
        HelloController2 test = new HelloController2(mockA);
        when(mockA.send(any())).thenReturn("answer");
        String ans = test.hello2();
        assertEquals(ans, "answer");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mockA).send(argument.capture());
        assertEquals(argument.getValue(), "hello2");
    }


}