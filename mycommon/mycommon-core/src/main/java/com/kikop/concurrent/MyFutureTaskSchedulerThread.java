package com.kikop.concurrent;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author kikop
 * @version 1.0
 * @project myconcurrent
 * @file MyFutureTaskSchedulerThread
 * @desc
 * @date 2021/4/26
 * @time 9:30
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
public class MyFutureTaskSchedulerThread extends Thread {

    // 控制 MyFutureTaskSchedulerThread的启停
    private static volatile boolean toStop = false;

    // 任务执行过程中线程休眠时间
    private long sleepTimeMs = 1000;

    // 静态变量
    // 类加载器加载中创建初始化
    // 任务执行
    private static MyFutureTaskSchedulerThread myFutureTaskSchedulerThread =
            new MyFutureTaskSchedulerThread("t-myFutureTaskSchedulerThread");


    // 当前线程的任务队列，获取的任务转换提交到线程池
    // 任务一级缓存 thread.executeTaskQueue-->MyThreadPoolHelper.executorServicePool
    private ConcurrentLinkedQueue<ExecuteTask> executeTaskQueue = new ConcurrentLinkedQueue<ExecuteTask>();

    /**
     * 在线程中运行
     */
    public MyFutureTaskSchedulerThread(String threadName) {
        super(threadName);
        MyThreadPoolHelper.toStart();
        this.start();
    }

    /**
     * 线程中调度
     */
    @Override
    public void run() {
        while (!toStop) {
            fetchTask(); // 处理任务
            threadSleep(sleepTimeMs);
        }
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */
    public static void addTask(ExecuteTask executeTask) {
        log.info("开始向任务池中加入一条异步任务");
        myFutureTaskSchedulerThread.executeTaskQueue.add(executeTask);
    }


    /**
     * stopTask
     * 1.停止全局静态线程
     * 2.停止线程池
     */
    public static void stopTask() {

        // 1.通过变量优雅的停止全局静态线程
        toStop = true;

        // 2.停止线程池
        MyThreadPoolHelper.toStop();
    }

    private void threadSleep(long timeMs) {
        try {
            sleep(timeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("threadSleep:{}", e);
        }
    }

    /**
     * 同步处理任务队列，检查其中是否有任务
     */
    private void fetchTask() {
        try {
            ExecuteTask executeTask;
            while (executeTaskQueue.peek() != null) { // 先判读是否有数据
                executeTask = executeTaskQueue.poll(); // 有数据
                processTask(executeTask);
            }
        } catch (Exception e) {
            log.error("fetchTask运行异常:{}", e);
        }
    }

    /**
     * 执行任务到线程池操作
     *
     * @param executeTask
     */
    private void processTask(ExecuteTask executeTask) {
        MyThreadPoolHelper.addTask(executeTask);
    }


}
