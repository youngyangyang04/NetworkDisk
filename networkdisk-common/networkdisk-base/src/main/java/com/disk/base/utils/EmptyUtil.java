package com.disk.base.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 类描述: 判空工具类
 *
 * @author weikunkun
 */
public class EmptyUtil {

    private EmptyUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof CharSequence && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        return false;
    }
}
