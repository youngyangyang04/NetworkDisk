package com.disk.common.distributed;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述: 基于redis实现的分布式锁
 *
 * @author weikunkun
 * @date 2024/2/25
 */
@Slf4j
public class RedisDistributedLock implements DistributedLock {

    /**默认锁超时时间为10S*/
    private static final int EXPIRE_SECONDS = 50;

    private RedisDistributedLock() {
    }

    private volatile static RedisDistributedLock redisDistributedLock;

    public static RedisDistributedLock getInstance() {
        if (redisDistributedLock == null) {
            synchronized (RedisDistributedLock.class) {
                redisDistributedLock = new RedisDistributedLock();
            }
        }
        return redisDistributedLock;
    }

    @Override
    public void lock(String lockName, long expireSeconds) {
        
    }

    @Override
    public Boolean tryLock(String lockName, long millisecond) {
        return null;
    }

    @Override
    public void unlock(String lockName) {

    }
}
