//package com.kikop.config;
//
//
//import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.SelectorType;
//import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
//import org.apache.rocketmq.spring.support.RocketMQUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.Assert;
//
///**
// * @author kikop
// * @version 1.0
// * @project myunreadserver
// * @file MyConsumerConfig
// * @desc
// * @date 2022/3/16
// * @time 9:30
// * @by IDE IntelliJ IDEA
// */
//@Configuration
//public class MyConsumerConfig {
//
//
//    /**
//     * RocketMQ配置文件
//     */
//    @Autowired
//    public RocketMQProperties rocketMQProperties;
//
//    /**
//     * 替代:DefaultMQPullConsumer
//     * @param rocketMQProperties
//     * @return
//     * @throws MQClientException
//     */
//    public DefaultLitePullConsumer defaultLitePullConsumer(RocketMQProperties rocketMQProperties) throws MQClientException {
//
//
//
//        RocketMQProperties.Consumer consumerConfig = rocketMQProperties.getConsumer();
//        String nameServer = rocketMQProperties.getNameServer();
//        String groupName = consumerConfig.getGroup();
//
//        // 指定消费的 topic 名称
//        String topicName = consumerConfig.getTopic();
//        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");
//        Assert.hasText(groupName, "[rocketmq.consumer.group] must not be null");
//        Assert.hasText(topicName, "[rocketmq.consumer.topic] must not be null");
//        String accessChannel = rocketMQProperties.getAccessChannel();
//        MessageModel messageModel = MessageModel.valueOf(consumerConfig.getMessageModel());
//
//        // String转换成需要的枚举,设计思路
//        SelectorType selectorType = SelectorType.valueOf(consumerConfig.getSelectorType());
//        String selectorExpression = consumerConfig.getSelectorExpression();
//        String ak = consumerConfig.getAccessKey();
//        String sk = consumerConfig.getSecretKey();
//        int pullBatchSize = consumerConfig.getPullBatchSize();
//        DefaultLitePullConsumer litePullConsumer = RocketMQUtil.createDefaultLitePullConsumer(nameServer, accessChannel, groupName, topicName, messageModel, selectorType, selectorExpression, ak, sk, pullBatchSize);
//        litePullConsumer.setEnableMsgTrace(consumerConfig.isEnableMsgTrace());
//        litePullConsumer.setCustomizedTraceTopic(consumerConfig.getCustomizedTraceTopic());
//
//        // 指定消费位置
//        // 选择从 queue 头部开始 pull 还是从尾部开始 pull
//        // litePullConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        litePullConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
//        return litePullConsumer;
//    }
//
//}