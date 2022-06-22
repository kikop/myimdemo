package com.kikop.mycomponent.custom.consumer;

import com.kikop.utils.DateUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file MyRocketMQListener
 * @desc 自定义业务处理消费端监听接口
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public class MyRocketMQListener implements RocketMQListener<List<MessageExt>> {

    /**
     * 接收mq消息
     *
     * @param messageExts
     */
    @Override
    public void onMessage(List<MessageExt> messageExts) { // 默认周期:5秒一次
        if (messageExts.size() >= 1) {
            System.out.println(String.format("%s 收到数据,数据量: %d", DateUtils.getTime(), messageExts.size()));
        } else {
            System.out.println(String.format("%s 没有可消费的数据", DateUtils.getTime()));
        }

    }
}
