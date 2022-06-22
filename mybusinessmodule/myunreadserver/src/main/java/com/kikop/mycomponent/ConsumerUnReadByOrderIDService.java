package com.kikop.mycomponent;


import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;


/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file ConsumerUnReadByOrderIDService
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
//@Component
@RocketMQMessageListener(topic = "TopicTest", consumerGroup = "please_rename_unique_group_name_4",
        selectorExpression = "TagA || TagC || TagD",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.ORDERLY)
public class ConsumerUnReadByOrderIDService implements RocketMQListener<MessageExt> {

    /**
     * MessageModel：集群模式；广播模式
     * ConsumeMode：顺序消费；无序消费
     */

    @Override
    public void onMessage(MessageExt msg) {
        System.out.println("----------ConsumerUnReadByTagService,接收到rocketmq tag消息:" + msg);

        // 可以看到每个queue有唯一的consume线程来消费,
        // 订单对每个queue(分区)有序
        System.out.println("consumeThread=" + Thread.currentThread().getName() + "queueId=" + msg.getQueueId()
                + ", content:" + new String(msg.getBody()));

    }
}
