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

    /**
     * 判断字符串是否为空，空串被视为空
     *
     * @param str
     * @return null或空串返回true
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 判断列表是否为空
     *
     * @param collection
     * @return null或空列表返回true
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 判断数组对象是否为空
     *
     * @param array
     * @return
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断对象是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        return null == obj || "".equals(obj);
    }

    /**
     * 判断Map是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Map<?, ?> obj) {
        return obj == null || obj.isEmpty();
    }

}