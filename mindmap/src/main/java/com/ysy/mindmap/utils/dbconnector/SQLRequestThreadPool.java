package com.ysy.mindmap.utils.dbconnector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class SQLRequestThreadPool {

    private static ThreadPoolExecutor pool;

    /**
     * 根据配置信息初始化线程池
     */
    static void init() {
        SQLClientConfig config = SQLClient.config;
        pool = new ThreadPoolExecutor(config.corePoolZie,
                config.maxPoolSize, config.keepAliveTime,
                config.timeUnit, config.blockingQueue);
    }

    /**
     * 执行任务
     */
    static void execute(final Runnable r) {
        if (r != null) {
            try {
                pool.execute(r);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清空阻塞队列
     */
    static void removeAllTask() {
        pool.getQueue().clear();
    }

    /**
     * 从阻塞队列中删除指定任务
     */
    static boolean removeTaskFromQueue(final Object obj) {
        if (!pool.getQueue().contains(obj)) {
            return false;
        }

        pool.getQueue().remove(obj);
        return true;
    }

    /**
     * 获取阻塞队列
     */
    static BlockingQueue<Runnable> getQueue() {
        return pool.getQueue();
    }

    /**
     * 关闭，并等待任务执行完成，不接受新任务
     */
    static void shutdown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    /**
     * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    static void shutdownRightNow() {
        if (pool != null) {
            pool.shutdownNow();
            try {
                // 设置超时极短，强制关闭所有任务
                pool.awaitTermination(1,
                        TimeUnit.MICROSECONDS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
