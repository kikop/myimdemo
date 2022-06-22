package com.kikop.utils;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project mycommon-rocketmq
 * @file ConsumerUtils
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Component
public class ConsumerUtils {

    // rocketMQTemplate 底层会自动去取
    // 容器中的 producer、consumer
    @Autowired
    public RocketMQTemplate rocketMQTemplate;

    /**
     * 接收同步可靠消息
     *
     * @param rocketMQListener
     * @param pollTimeoutMills 如果有数据的化,在范围内,会一直poll的,注意,好设计,空闲时,每隔多长时间从队列里面去消费数据
     * @throws MQClientException
     */
    public void getSyncMessage(RocketMQListener<List<MessageExt>> rocketMQListener, long pollTimeoutMills) throws MQClientException {

        // 启动线程进行消费,否则会阻塞其他 Bean的创建
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultLitePullConsumer defaultLitePullConsumer = rocketMQTemplate.getConsumer();
                    while (defaultLitePullConsumer.isRunning()) {

                        // poll:不阻塞
                        // poll by timeout:可中断,释放cpu时间片,到指定的超时时间会被唤醒
                        // 此方法从此LinkedBlockingQueue的头部检索并删除元素，如果在元素可用之前经过了指定的等待时间，则为null。
                        List<MessageExt> messageExts = defaultLitePullConsumer.poll(pollTimeoutMills);

                        // 1.业务回调内容
                        rocketMQListener.onMessage(messageExts);
                        // 2.消费完手动提交(默认就是)
                        // defaultLitePullConsumer.commitSync();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }


}