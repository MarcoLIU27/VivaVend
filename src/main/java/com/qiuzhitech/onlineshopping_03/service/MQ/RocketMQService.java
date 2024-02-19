package com.qiuzhitech.onlineshopping_03.service.MQ;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String body) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, body.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

    public void sendDelayMessage(String topic, String body, int delayLevel) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        Message message = new Message(topic, body.getBytes());
        message.setDelayTimeLevel(delayLevel);
        rocketMQTemplate.getProducer().send(message);
    }
}

