package com.kikop.mycomponent.custom.consumer;


import com.kikop.utils.ConsumerUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;


/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file ConsumerUnReadServiceByCommon
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
//@Component
public class ConsumerUnReadServiceByCommon {

    /**
     * 如果有数据的化,在范围内,会一直poll的
     * 注意,好设计,空闲时,每隔多长时间从队列里面去消费数据
     */
    @Value("${myunread.consumer.polltimeoutmills}")
    private long pollTimeOutMills;

    @Autowired
    public ConsumerUtils consumerUtils;

    @PostConstruct
    public void regisConsumerData() throws MQClientException {
        consumerUtils.getSyncMessage(new MyRocketMQListener(),pollTimeOutMills);
    }
}