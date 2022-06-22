package com.kikop;

import com.kikop.model.OrderStep;
import com.kikop.service.UnReadService;
import com.kikop.utils.spring.SpringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author kikop
 * @version 1.0
 * @project myunreadserver
 * @file MyUnreadServerApplication
 * @desc
 * @date 2022/3/17
 * @time 16:30
 * @by IDE IntelliJ IDEA
 */
@SpringBootApplication
@Controller
// 注解扫描多个包下示例,内嵌包中有@Component注解,需开启如下内容
//@ComponentScan({"com.kikopxxx", "com.kikop"})
public class MyUnreadServerApplication implements CommandLineRunner {


    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(MyUnreadServerApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

//        syncSendMsgTest();
//        asyncSendMsgTest();
//        sendMsgByOrderTest();
//        sendDelayMsgTest();
//        sendTransactionMsg();


    }

    /**
     * 发送带属性的消息
     */
    public void sendTransactionMsg() {
        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        unReadService.sendTransactionMsg();
    }

    /**
     * 发送带属性的消息
     */
    public void sendMsgByProperties() {
        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        unReadService.sendMsgByProperties();
    }

    /**
     * 同步发送消息
     *
     * @throws InterruptedException
     */
    public void syncSendMsgTest() throws InterruptedException {
        // 1.发送带tag的同步消息
        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        for (int i = 0; i < 3; i++) {
            unReadService.syncSendMsg(String.format("你好,我的名字叫大海:%d!", i));
            TimeUnit.SECONDS.sleep(3);
        }
    }

    /**
     * 异步发送消息
     *
     * @throws InterruptedException
     */
    public void asyncSendMsgTest() throws InterruptedException {
        // 1.发送带tag的同步消息
        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        for (int i = 0; i < 3; i++) {
            unReadService.asyncSendMsg(String.format("你好,我的名字叫异步大海:%d!", i));
            TimeUnit.SECONDS.sleep(3);
        }
    }

    /**
     * 通过队列选择算法订单发送到同一队列
     */
    public void sendMsgByOrderTest() {

        String[] tags = new String[]{"TagA", "TagC", "TagD"};

        // 订单列表
        List<OrderStep> orderList = buildOrders();

        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        unReadService.sendMsgByOrder(orderList, tags);
    }

    /**
     * 发送延迟消息(特别是等待支付场景)
     *
     * @throws InterruptedException
     */
    public void sendDelayMsgTest() throws InterruptedException {
        // 1.发送带tag的同步消息
        UnReadService unReadService = SpringUtils.getBean(UnReadService.class);
        unReadService.sendDelayMsg("mydelaytopic001");

    }


    /**
     * 生成模拟订单数据
     */
    private List<OrderStep> buildOrders() {
        List<OrderStep> orderList = new ArrayList<OrderStep>();

        OrderStep orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("推送");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        return orderList;
    }


}
