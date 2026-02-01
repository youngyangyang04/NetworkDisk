package com.disk.base.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import com.disk.base.constant.BaseConstant;
import com.disk.base.exception.BizException;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类描述: 雪花算法 id 生成器
 *
 * @author weikunkun
 */
public class IdUtil {

    /**
     * 工作id 也就是机器id
     */
    private static long workerId;

    /**
     * 数据中心id
     */
    private static long dataCenterId;

    /**
     * 序列号
     */
    private static long sequence;

    /**
     * 初始时间戳
     */
    private static long startTimestamp = 1288834974657L;

    /**
     * 工作id长度为5位
     */
    private static long workerIdBits = 5L;

    /**
     * 数据中心id长度为5位
     */
    private static long dataCenterIdBits = 5L;

    /**
     * 工作id最大值
     */
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 数据中心id最大值
     */
    private static long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /**
     * 序列号长度
     */
    private static long sequenceBits = 12L;

    /**
     * 序列号最大值
     */
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作id需要左移的位数，12位
     */
    private static long workerIdShift = sequenceBits;

    /**
     * 数据id需要左移位数 12+5=17位
     */
    private static long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳需要左移位数 12+5+5=22位
     */
    private static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 上次时间戳，初始值为负数
     */
    private static long lastTimestamp = -1L;

    static {
        workerId = getMachineNum() & maxWorkerId;
        dataCenterId = getMachineNum() & maxDataCenterId;
        sequence = 0L;
    }

    /**
     * 获取机器编号
     *
     * @return
     */
    private static long getMachineNum() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

    /**
     * 获取时间戳，并与上次时间戳比较
     *
     * @param lastTimestamp
     * @return
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取系统时间戳
     *
     * @return
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 生成ID
     *
     * @return
     */
    public synchronized static Long get() {
        long timestamp = timeGen();
        // 获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 获取当前时间戳如果等于上次时间戳
        // 说明：还处在同一毫秒内，则在序列号加1；否则序列号赋值为0，从0开始。
        // 0 - 4095
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - startTimestamp) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 加密ID
     * @param id long 型
     * @return String 型 的加密 id 字符串
     */
    public static String encrypt(Long id) {
        if (Objects.nonNull(id)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putLong(0, id);
            byte[] content = byteBuffer.array();
            byte[] encrypt = AESUtil.aesEncrypt(content);
            return Base64.encode(encrypt);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解密ID
     *
     * @param decryptId 加密 id 字符串
     * @return long 型的 id
     */
    public static Long decrypt(String decryptId) {
        if (StringUtils.isNotBlank(decryptId)) {
            byte[] encrypt = Base64.decode(decryptId);
            byte[] content = AESUtil.aesDecode(encrypt);
            if (ArrayUtil.isNotEmpty(content)) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(content);
                return byteBuffer.getLong();
            }
            throw new BizException("AES128Util.aesDecode fail");
        }
        throw new BizException("the decryptId can not be empty");
    }

    /**
     * 解密多个加密ID字符串拼接的字符串，拼接间隔是公用的字符串分隔符
     * @param decryptIdStr
     * @return
     */
    public static List<Long> decryptIdList(String decryptIdStr) {
        if (StringUtils.isBlank(decryptIdStr)) {
            return Lists.newArrayList();
        }
        List<String> decryptIdList = Splitter.on(BaseConstant.COMMON_SEPARATOR).splitToList(decryptIdStr);
        if (CollectionUtils.isEmpty(decryptIdList)) {
            return Lists.newArrayList();
        }
        List<Long> result = decryptIdList.stream().map(IdUtil::decrypt).collect(Collectors.toList());
        return result;
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.encrypt(1L));
        System.out.println(IdUtil.encrypt(-1L));
    }
}
