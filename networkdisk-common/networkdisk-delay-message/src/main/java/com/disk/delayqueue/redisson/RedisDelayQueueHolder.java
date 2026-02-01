package com.disk.delayqueue.redisson;

import com.disk.delayqueue.DelayQueueHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 类描述: Redis延迟队列持有者
 *
 * @author weikunkun
 */
@Slf4j
@Component
@ConditionalOnClass(RedissonClient.class) //只有容器中存在RedissonClient才初始化到容器中
public class RedisDelayQueueHolder implements DelayQueueHolder {
    @Autowired
    private RedissonClient redissonClient;

    //延迟队列本地缓存
    private Map<String, RDelayedQueue<Object>> delayedQueueMap = new ConcurrentHashMap<>(16);

    /**
     * 添加到消息队列
     *
     * @param value     添加到队列的对象
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     * @param queueName 队列名称
     * @param <T>
     */
    @Override
    public <T> void addJob(T value, long delay, TimeUnit timeUnit, String queueName) {
        try {
            log.info("添加到延时队列【{}】【{}-{}】【{}】", value, delay, timeUnit.name(), queueName);
            RDelayedQueue<Object> delayedQueue = this.initDelayQueue(queueName);
            delayedQueue.offer(value, delay, timeUnit);
        } catch (Exception e) {
            log.error("添加到延时队列失败： value:{}  queueName:{} error:{}", value, queueName, e);
            throw new RuntimeException("添加到延时队列失败");
        }
    }

    /**
     * 添加延迟任务，仅当不存在相同任务时添加延迟队列
     *
     * @param value
     * @param delay
     * @param timeUnit
     * @param queueName
     */
    @Override
    public <T> void addJobIfAbsent(T value, long delay, TimeUnit timeUnit, String queueName) {
        boolean contained = contains(value, queueName);
        if (!contained) {
            addJob(value, delay, timeUnit, queueName);
        }
    }

    /**
     * @param value     队列的对象
     * @param queueName 队列名称
     * @return
     */
    @Override
    public boolean removeJob(Object value, String queueName) {
        RDelayedQueue<Object> delayedQueue = this.initDelayQueue(queueName);
        return delayedQueue.remove(value);
    }

    /**
     * 队列中是否包含某个值
     *
     * @param value
     * @param queueName
     * @return
     */
    @Override
    public boolean contains(Object value, String queueName) {
        RDelayedQueue<Object> delayedQueue = this.initDelayQueue(queueName);
        return delayedQueue.contains(value);

    }

    /**
     * 取值
     */
    @Override
    public RBlockingDeque getQueue(String queueName) {
        /*
            应用重启后，如果没有新的消息添加到延迟队列时，会没有初始化延迟队列，会导致以前在队列里的消息不能消费。
            所以这里每次获取时，默认初始化一次。
         */
        this.initDelayQueue(queueName);
        return redissonClient.getBlockingDeque(queueName);
    }

    /**
     * 根据QueueName获取包装类
     *
     * @param queueName
     * @return
     */
    @Override
    public <T> T take(String queueName) throws InterruptedException {
        return (T) getQueue(queueName).take();
    }

    /**
     * 延迟队列初始化
     *
     * @param queueName
     * @return
     */
    private RDelayedQueue<Object> initDelayQueue(String queueName) {
        RDelayedQueue<Object> delayedQueue = delayedQueueMap.get(queueName);
        if (null == delayedQueue) {
            RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(queueName);
            delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            delayedQueueMap.put(queueName, delayedQueue);
        }
        return delayedQueue;
    }

}
