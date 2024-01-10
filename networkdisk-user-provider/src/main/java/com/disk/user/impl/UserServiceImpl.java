package com.disk.user.impl;

import com.disk.api.ServiceAPI;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 * @date 2024/2/25
 */
@DubboService
public class UserServiceImpl implements ServiceAPI {
    @Override
    public String sendMsg(String msg) {
        return "test message: " + msg;
    }
}
