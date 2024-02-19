package com.qiuzhitech.onlineshopping_03.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class HelloControllerTest {


    HelloController helloController;

    @Mock
    DependencyA fakeDependencyA;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void helloTest() {
        helloController = new HelloController(new DependencyA());
        String res = helloController.hello();
        assertEquals("Hello world!!!", res);
    }

    @Test
    void helloMockTest() {
        helloController = new HelloController(fakeDependencyA);
        when(fakeDependencyA.send(any()))
                .thenReturn("ABC");
        String res = helloController.hello();
        assertEquals("ABC", res);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(fakeDependencyA).send(argument.capture());
        assertEquals("Hello world!!!", argument.getValue());
    }

}