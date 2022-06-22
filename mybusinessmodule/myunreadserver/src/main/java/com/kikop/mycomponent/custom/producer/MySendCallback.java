package com.kikop.mycomponent.custom.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

public class MySendCallback implements SendCallback {

    private int index;

    public MySendCallback(int index) {
        this.index = index;
    }

    @Override
    public void onSuccess(SendResult sendResult) {
        System.out.printf("%-10d OK %s %n", index,
                sendResult.getMsgId());
    }

    @Override
    public void onException(Throwable e) {
        System.out.printf("%-10d Exception %s %n", index, e);
        e.printStackTrace();
    }
}
