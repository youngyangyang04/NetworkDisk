package com.disk.delayqueue.executor;

import com.disk.delayqueue.DelayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类描述: 延迟队列顶层接口
 *
 * @author weikunkun
 */
public interface DelayQueueExecutor<T extends DelayMessage> {
    Logger log = LoggerFactory.getLogger(DelayQueueExecutor.class);
    /**
     * 延迟队列名称
     *
     * @return
     */
    String queueName();

    /**
     * 执行业务
     *
     * @param t
     */
    void execute(T t);

    /**
     * 获取延迟消息
     *
     * @return
     * @throws InterruptedException
     */
    T take() throws InterruptedException;

}
