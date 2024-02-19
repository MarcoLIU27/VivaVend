package com.qiuzhitech.onlineshopping_03.service.MQ;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestConsumerListenerTest {
    @Resource
    RocketMQService rocketMQService;

    @Test
    void onMessage() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        rocketMQService.sendMessage("testTopic", "This is a test body.");
    }
}