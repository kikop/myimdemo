package com.kikop.mycomponent;


import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file MyConsumerConfig
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
//@Component
@RocketMQMessageListener(topic = "${myunread.topic}", consumerGroup = "${myunread.consumer.group2}",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
public class ConsumerUnReadService implements RocketMQListener<String> {

    /**
     * MessageModel：集群模式；广播模式
     * ConsumeMode：顺序消费；无序消费
     */
    @Override
    public void onMessage(String message) {
        System.out.println("----------ConsumerUnReadService,接收到rocketmq消息:" + message);

        // rocketmq会自动捕获异常回滚  (官方默认会重复消费16次)
        // int a = 1 / 0;
    }
}
