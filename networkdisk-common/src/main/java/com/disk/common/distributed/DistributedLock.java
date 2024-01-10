package com.disk.common.distributed;

/**
 * 类描述: 分布式锁
 *
 * @author weikunkun
 * @date 2024/1/14
 */
public interface DistributedLock {

    /**
     * 加锁
     * @throws Exception
     */
    void lock(String lockName, long expireSeconds);


    Boolean tryLock(String lockName, long millisecond);

    void unlock(String  lockName);
}
