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
// 默认是 pull 模式,但 pull 不支持 The broker does not support consumer to filter message by SQL92
//@Component
@RocketMQMessageListener(topic = "${myunread.topic}", consumerGroup = "${myunread.consumer.group}",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY,
        selectorType = SelectorType.TAG,
        selectorExpression = "${myunread.selectorExpression}"
)
public class ConsumerUnReadByPropertiesMsgService implements RocketMQListener<MessageExt> {

    /**
     * MessageModel：集群模式；广播模式
     * ConsumeMode：顺序消费；无序消费
     */

    @Override
    public void onMessage(MessageExt message) {
//        for (MessageExt message : messages) {
        // Print approximate delay time period

        System.out.println("Receive message[msgId=" + message.getMsgId() + "] "
                + (System.currentTimeMillis() - message.getBornTimestamp()) + "ms later");

//        System.out.println("Receive message[msgId=" + message.getMsgId() + "] "
//                + (System.currentTimeMillis() - message.getStoreTimestamp()) + "ms later" + DateUtils.getTime());
//        }

    }
}
