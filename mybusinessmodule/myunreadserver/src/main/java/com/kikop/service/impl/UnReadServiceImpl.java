package com.kikop.service.impl;

import com.kikop.model.OrderStep;
import com.kikop.mycomponent.custom.producer.MySendCallback;
import com.kikop.service.UnReadService;
import com.kikop.utils.DateUtils;
import com.kikop.utils.ProducerUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file UnReadServiceImpl
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Component
public class UnReadServiceImpl implements UnReadService {

    @Value("${myunread.topic}")
    public String topic;

    @Value("${myunread.selectorExpression}")
    public String tag;

    @Autowired
    public RocketMQProperties rocketMQProperties;

    @Autowired
    public ProducerUtils producerUtil;

    @Autowired
    public RocketMQTemplate rocketMQTemplate;

    /**
     * 指定需要些的topic(或+tag)
     *
     * @param strMsg
     */
    @Override
    public void syncSendMsg(String strMsg) {

        System.out.println(String.format("%s start syncSendMsg", DateUtils.getTime()));

        SendResult sendResult = producerUtil.syncSend(rocketMQProperties.getConsumer().getTopic()
                + ":" + rocketMQProperties.getConsumer().getSelectorExpression(), strMsg);
        System.out.println(String.format("%s getMsgId:%s", DateUtils.getTime(), sendResult.getMsgId()));

        System.out.println(String.format("%s end syncSendMsg", DateUtils.getTime()));

    }

    /**
     * 异步发送消息
     * 指定需要些的topic(或+tag)
     *
     * @param strMsg
     */
    @Override
    public void asyncSendMsg(String strMsg) {

        System.out.println(String.format("%s start asyncSendMsg", DateUtils.getTime()));

        producerUtil.asyncSend(rocketMQProperties.getConsumer().getTopic()
                        + ":" + rocketMQProperties.getConsumer().getSelectorExpression(), strMsg,
                new MySendCallback(123));
        System.out.println(String.format("%s end asyncSendMsg", DateUtils.getTime()));

    }

    @Override
    public void sendMsgByOrder(List<OrderStep> orderList, String[] tags) {
// 顺序消息样例
//        消息有序指的是可以按照消息的发送顺序来消费(FIFO)。RocketMQ可以严格的保证消息有序，可以分为分区有序或者全局有序。
//
//        顺序消费的原理解析，在默认的情况下消息发送会采取Round Robin轮询方式把消息发送到不同的queue(分区队列)；而消费消息的时候从多个queue上拉取消息，这种情况发送和消费是不能保证顺序。但是如果控制发送的顺序消息只依次发送到同一个queue中，消费的时候只从这个queue上依次拉取，则就保证了顺序。当发送和消费参与的queue只有一个，则是全局有序；如果多个queue参与，则为分区有序，即相对每个queue，消息都是有序的。
//
//        下面用订单进行分区有序的示例。一个订单的顺序流程是：创建、付款、推送、完成。订单号相同的消息会被先后发送到同一个队列中，消费时，同一个OrderId获取到的肯定是同一个队列。
        DefaultMQProducer producer = rocketMQTemplate.getProducer();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        for (int i = 0; i < 10; i++) {

            // 加个时间前缀
            String body = dateStr + " Hello RocketMQ " + orderList.get(i);
            Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i, body.getBytes());

            SendResult sendResult = null; //订单id
            try {
                sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        Long id = (Long) arg;  // 根据订单id选择发送queue
                        long index = id % mqs.size();
                        return mqs.get((int) index);
                    }
                }, orderList.get(i).getOrderId());

                System.out.println(String.format("SendResult status:%s, queueId:%d, body:%s",
                        sendResult.getSendStatus(),
                        sendResult.getMessageQueue().getQueueId(),
                        body));

            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


    @Override
    public void sendDelayMsg(String delayTopic) {

//        ###  延时消息的使用场景
//
//        比如电商里，提交了一个订单就可以发送一个延时消息，1h后去检查这个订单的状态，如果还是未付款就取消订单释放库存。

        DefaultMQProducer producer = rocketMQTemplate.getProducer();

        int totalMessagesToSend = 1;
        for (int i = 0; i < totalMessagesToSend; i++) {

            // 1.构建Message
            Message message = new Message(delayTopic, ("Hello scheduled message " + i).getBytes());
            // 2.设置延时等级3
            // 这个消息将在 10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
            // 消息有两个时间,

            // org/apache/rocketmq/store/config/MessageStoreConfig.java
//            private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
            if (i == 0) {
                message.setDelayTimeLevel(5); // 30s
            } else {
                message.setDelayTimeLevel(2); // 5s,每个 consumerQueue天生是保持顺序性插入的的
            }
            // 3.发送消息
            try {
                producer.send(message);
                System.out.println("data send ok!" + DateUtils.getTime());
            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void sendMsgByProperties() {
        DefaultMQProducer producer = rocketMQTemplate.getProducer();

        Message msg = null;


        for (int i = 1; i < 6; i++) {
            try {
                msg = new Message(topic, tag, // Tag:jack
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );

                // 设置一些属性(部分SQL支持)
                msg.putUserProperty("version", String.valueOf(i));
                try {
                    SendResult sendResult = producer.send(msg);
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    }


    //  默认的 transProducer
    @Autowired
    public TransactionMQProducer transactionMQProducer;


    @Override
    public void sendTransactionMsg() {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};

        for (int i = 0; i < 1; i++) {
            try {
                Message msg =
                        new Message("TopicTest1234", tags[i % tags.length], "KEY" + i,
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

                // 发送的时候会调用,executeLocalTransaction
                SendResult sendResult = transactionMQProducer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 100000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
