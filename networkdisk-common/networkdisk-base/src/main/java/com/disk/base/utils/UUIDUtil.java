package com.disk.base.utils;

import java.util.UUID;

/**
 * 类描述: UUID工具类
 *
 * @author weikunkun
 */
public class UUIDUtil {
    public static final String EMPTY_STR = "";
    public static final String HYPHEN_STR = "-";

    /**
     * 获取UUID字符串
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace(HYPHEN_STR, EMPTY_STR).toUpperCase();
    }
}
