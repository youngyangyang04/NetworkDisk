package com.disk.delayqueue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 类描述: 延迟队列持有者顶层接口
 * 延迟队列持有者顶层接口
 * <p>
 * 后续可以通过实现以下接口，实现不同的实现，如基于redission的延迟队列实现，或者如java原生的延迟队列实现。
 * @author weikunkun
 * @date 2025/01/20
 */
public interface DelayQueueHolder {
    /**
     * 添加延迟任务
     *
     * @param value
     * @param delay
     * @param timeUnit
     * @param queueName
     * @param <T>
     */
    <T> void addJob(T value, long delay, TimeUnit timeUnit, String queueName);

    /**
     * 添加延迟任务，仅当不存在时添加延迟队列任务
     *
     * @param value
     * @param delay
     * @param timeUnit
     * @param queueName
     * @param <T>
     */
    <T> void addJobIfAbsent(T value, long delay, TimeUnit timeUnit, String queueName);

    /**
     * 移除延迟任务
     *
     * @param value
     * @param queueName
     * @param <T>
     * @return
     */
    <T> boolean removeJob(T value, String queueName);

    /**
     * 是否存在了延迟任务
     *
     * @param value
     * @param queueName
     * @param <T>
     * @return
     */
    <T> boolean contains(T value, String queueName);

    /**
     * 获取延迟任务队列
     *
     * @param queueName
     * @return
     */
    BlockingDeque getQueue(String queueName);

    /**
     * 根据QueueName获取包装类
     *
     * @param queueName
     * @param <T>
     * @return
     * @throws InterruptedException 中断异常
     */
    <T> T take(String queueName) throws InterruptedException;
}
