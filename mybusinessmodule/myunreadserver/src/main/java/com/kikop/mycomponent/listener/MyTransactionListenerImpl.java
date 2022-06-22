package com.kikop.mycomponent.listener;

import com.kikop.utils.DateUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MyTransactionListenerImpl implements TransactionListener {
    private AtomicInteger transactionIndex = new AtomicInteger(0);

    // key:transactionId
    // value:state
    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    /**
     * 执行本地事务
     * 当发送半消息成功时,根据返回值决定是否提交事务,只执行一次
     *
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.println(String.format("开始执行本地事务,msgId:%s, executeLocalTransaction:%s",msg.getBuyerId(),DateUtils.getTime()));
        //  直接模拟事务执行结果未知,触发mq事务回查
        int value = transactionIndex.getAndIncrement();
//        int status = value % 3;
        int status = 0;
        localTrans.put(msg.getTransactionId(), status);
        return LocalTransactionState.UNKNOW;
    }

    /**
     * 如果已经成功,则不需再次检查
     * 获取本地事务状态
     * 默认检查15次,周期:1分钟
     * 为了避免单个消息被检查太多次而导致半队列消息累积，我们默认将单个消息的检查次数限制为 15 次
     *
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println(String.format("开始执行事务回查,msgId:%s, checkLocalTransaction:%s",msg.getMsgId(),DateUtils.getTime()));
        Integer status = localTrans.get(msg.getTransactionId());
        if (null != status) {
            switch (status) {
                case 0:
                    return LocalTransactionState.UNKNOW;
                case 1:
                    return LocalTransactionState.COMMIT_MESSAGE;
                case 2:
                    return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
