package com.kikop.utils;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-rocketmq
 * @file ProducerUtils
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Component
public class ProducerUtils {

    // 前提容器中有:producer、consumer
    // rocketMQTemplate 底层会自动去取
    @Autowired
    public RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步可靠消息
     *
     * @param topic
     * @param strMsg
     * @return
     */
    public SendResult syncSend(String topic, String strMsg) {

        /**
         * 发送可靠同步消息 ,可以拿到SendResult 返回数据
         * 同步发送是指消息发送出去后，会在收到mq发出响应之后才会发送下一个数据包的通讯方式。
         * 这种方式应用场景非常广泛，例如重要的右键通知、报名短信通知、营销短信等。
         *
         * 参数1： topic:tag
         * 参数2:  消息体 可以为一个对象
         * 参数3： 超时时间 毫秒
         */
        SendResult result = rocketMQTemplate.syncSend(topic, strMsg, 10000);
        return result;
    }

    /**
     * 异步消息发送
     *
     * @param topic
     * @param strMsg
     * @param sendCallback
     */
    public void asyncSend(String topic, String strMsg, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, strMsg, sendCallback);
    }

    /**
     * 单向消息发送
     *
     * @param topic
     * @param strMsg
     */
    public void singleDirectionSend(String topic, String strMsg) {
        rocketMQTemplate.sendOneWay(topic, strMsg);
    }
}