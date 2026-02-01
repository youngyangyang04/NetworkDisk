package com.disk.base.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class PasswordUtil {


    /**
     * 加密 password
     *
     * @param salt
     * @param inputPassword
     * @return
     */
    public static String encryptPassword(String password) {
        return DigestUtil.md5Hex(password);
    }
}
