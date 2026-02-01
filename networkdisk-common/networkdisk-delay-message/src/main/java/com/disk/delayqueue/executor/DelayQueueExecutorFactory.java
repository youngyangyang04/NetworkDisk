package com.disk.delayqueue.executor;

import com.disk.delayqueue.DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述: 延迟队列执行器工厂类
 *
 * @author weikunkun
 */
@Slf4j
@Component
public class DelayQueueExecutorFactory implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 延迟队列执行器集合
     */
    public static Map<String, DelayQueueExecutor> executorBeanMap = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("delayQueueThreadPoolExecutor")
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, DelayQueueExecutor> delayQueueExecutorMap = event.getApplicationContext().getBeansOfType(DelayQueueExecutor.class);
        delayQueueExecutorMap.forEach((k, v) -> {
            String queueName = v.queueName();
            if (executorBeanMap.containsKey(queueName)) {
                DelayQueueExecutor delayQueueExecutor = executorBeanMap.get(queueName);
                throw new IllegalStateException("已存在相同队列的DelayQueueExecutor实例[" + queueName + "]:"
                        + " ---> \n" + delayQueueExecutor.getClass()
                        + " ---> \n" + v.getClass());
            }
            executorBeanMap.put(queueName, v);
            this.runExecutor(k, v);
        });
    }

    private void runExecutor(String queueName, DelayQueueExecutor executor) {
        //由于这个是固定永久循环监听一个队列，为了不占用通用线程池的线程，单独开启一个线程作为后台运行
        Thread thread = new Thread(() -> {
            log.info("延迟队列「{}」执行器已启动...", queueName);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DelayMessage delayMessage = executor.take();
                    log.info("获取到队列「{}」到期消息：{}", queueName, delayMessage);
                    if (Objects.nonNull(delayMessage)) {
                        threadPoolExecutor.execute(() -> executor.execute(delayMessage));
                    }
                } catch (RedissonShutdownException ex) {
                    log.error("收到 RedissonShutdownException 结束延迟队列[{}]异常,{}", queueName,ex);
                } catch (Exception e) {
                    log.error("延迟队列「{}」取值[{}]中断异常：{},  ", queueName, e.getMessage(), e);
                }
            }
            log.info("延迟队列线程名:{}, 被异常中断结束", Thread.currentThread().getName());
        });
        thread.setName("DelayQueueMainThread-" + queueName);
        thread.setDaemon(true);
        thread.start();
    }
}
