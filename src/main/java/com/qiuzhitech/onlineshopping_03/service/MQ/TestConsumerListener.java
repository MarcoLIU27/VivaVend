package com.qiuzhitech.onlineshopping_03.service.MQ;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = "testTopic",consumerGroup = "testGroup")
public class TestConsumerListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info(message);
    }
}
