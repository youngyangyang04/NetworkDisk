package com.disk.base.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author weikunkun
 */
public class ThreadPoolUtils {
    private static ThreadFactory smsSendFactory = new ThreadFactoryBuilder()
            .setNameFormat("sms-pool-%d").build();

    private static ExecutorService smsSendPool = new ThreadPoolExecutor(5, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), smsSendFactory, new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService getSmsSendPool() {
        return smsSendPool;
    }
}
