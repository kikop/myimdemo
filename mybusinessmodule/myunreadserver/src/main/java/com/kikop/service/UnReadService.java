package com.kikop.service;


import com.kikop.model.OrderStep;

import java.util.List;

/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file UnReadService
 * @desc
 * @date 2022/3/16
 * @time 9:30
 * @by IDE IntelliJ IDEA
 */
public interface UnReadService {

    void syncSendMsg(String strMsg);

    void asyncSendMsg(String strMsg);

    void sendMsgByOrder(List<OrderStep> orderList, String[] tags);

    void sendDelayMsg(String delayTopic);

    /**
     * 发送带属性的消息
     */
    void sendMsgByProperties();

    void sendTransactionMsg();
}
