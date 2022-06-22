package com.kikop.config;


import com.kikop.mycomponent.listener.MyTransactionListenerImpl;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file MyProducerConfig
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
@Configuration
public class MyProducerConfig {

//    @Value("${myunread.topic}")
//    public String topic;
//
//    @Value("${myunread.producer.group}")
//    public String strProducerGroup;


    /**
     * RocketMQ配置文件
     */
    @Autowired
    public RocketMQProperties rocketMQProperties;

    /**
     * 优先业务端的Bean
     * @return
     */
    @Bean
    DefaultMQProducer defaultMQProducer() {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = producerConfig.getGroup();
        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "[rocketmq.producer.group] must not be null");
        String accessChannel = rocketMQProperties.getAccessChannel();
        String ak = rocketMQProperties.getProducer().getAccessKey();
        String sk = rocketMQProperties.getProducer().getSecretKey();
        boolean isEnableMsgTrace = rocketMQProperties.getProducer().isEnableMsgTrace();
        String customizedTraceTopic = rocketMQProperties.getProducer().getCustomizedTraceTopic();
        DefaultMQProducer producer = RocketMQUtil.createDefaultMQProducer(groupName, ak, sk, isEnableMsgTrace, customizedTraceTopic);


        producer.setNamesrvAddr(nameServer);
        if (!StringUtils.isEmpty(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }

        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        // 发送失败,重试次数
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

        // 默认的topic: TBW102,自动创建的topci会继承该衣钵, 否则 No route info of this topic: ur-unread-topic
        // producer.setCreateTopicKey(rocketMQProperties.getConsumer().getTopic()
        //          + ":" + rocketMQProperties.getConsumer().getSelectorExpression());

        // 官方生产端无法预先指定topic
        return producer;
    }

    /**
     * 这个事务Producer继承:DefaultMQProducer
     * 有该Bean以后,则默认的就没有了,生产者只能有一个
     * 只能保留一个吗
     *
     * @return
     */
    @Bean
    public TransactionMQProducer transactionMQProducer() {
        String nameServer = rocketMQProperties.getNameServer();
        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");


        TransactionListener transactionListener = new MyTransactionListenerImpl();
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer("please_rename_unique_group_name");
        transactionMQProducer.setNamesrvAddr(nameServer);
        // 自定义 ExecutorService,即 checkExecutor
        ExecutorService executorService = new ThreadPoolExecutor(2, 5,
                100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("client-transaction-msg-check-thread");
                        return thread;
                    }
                });
        transactionMQProducer.setExecutorService(executorService);
        transactionMQProducer.setTransactionListener(transactionListener);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return transactionMQProducer;
    }

}