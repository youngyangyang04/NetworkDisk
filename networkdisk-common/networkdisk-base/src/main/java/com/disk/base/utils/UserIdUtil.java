package com.disk.base.utils;

import com.disk.base.constant.BaseConstant;

import java.util.Objects;

/**
 * 类描述: 获取用户信息
 *
 * @author weikunkun
 */
public class UserIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     *
     * @param userId
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     *
     * @return
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return BaseConstant.ZERO_LONG;
        }
        return userId;
    }
}
