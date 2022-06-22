package com.kikop.concurrent;


import java.util.concurrent.*;

/**
 * @author kikop
 * @version 1.0
 * @project myconcurrent
 * @file MyThreadPoolHelper
 * @desc
 * @date 2021/4/26
 * @time 9:30
 * @by IDE: IntelliJ IDEA
 */
public class MyThreadPoolHelper {

    // 停止线程池的等待时间
    private final long AWAIT_TIMEMS_ON_STOP = 5000;

    // 线程池
    // 任务二级缓存
    // executorServicePool,LinkedBlockingQueue
    private ExecutorService executorServicePool = null;

    private void init() {

        // 创建一个固定10个的线程池
        executorServicePool = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) { // 好处就是易于问题错误排查
                return new Thread(r, "myfutureTaskSchedulerThread-Pool-" + r.hashCode());
            }
        });
    }

    private void stop() {

        // 2.停止线程池
        // 2.1.停止线程池新任务的接收
        executorServicePool.shutdown();

        // 2.2.等待指定的时间,ms
        try {
            // Wait a while for existing tasks to terminate.
            if (!executorServicePool.awaitTermination(AWAIT_TIMEMS_ON_STOP, TimeUnit.MILLISECONDS)) {
                executorServicePool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled.
                if (!executorServicePool.awaitTermination(AWAIT_TIMEMS_ON_STOP, TimeUnit.MILLISECONDS)) {
//                    log.warn(String.format("%s didn't terminate!", executor));
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            // (Re-)Cancel if current thread also interrupted.
            executorServicePool.shutdownNow();
            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    // 对外暴露
    // 对象实例的创建在内部完成,便于外部使用
    private static MyThreadPoolHelper myThreadPoolHelper = new MyThreadPoolHelper();

    public static void toStart() {
        myThreadPoolHelper.init();
    }

    public static void toStop() {
        myThreadPoolHelper.stop();
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */
    public static void addTask(ExecuteTask executeTask) {
        myThreadPoolHelper.executorServicePool.execute(new ExecuteTaskRunnable(executeTask));
    }

    /**
     * ConvertExecuteTask2ExecuteTaskRunnable
     */
    private static class ExecuteTaskRunnable implements Runnable {

        ExecuteTask executeTask;

        ExecuteTaskRunnable(ExecuteTask executeTask) {
            this.executeTask = executeTask;
        }

        /**
         * 调度到某一个具体的线程池里
         */
        public void run() {
            executeTask.execute();
        }
    }
}
