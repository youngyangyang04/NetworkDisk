package com.disk.files.infrastructure.canal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Canal 增量同步配置
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "com.disk.canal")
public class CanalSyncProperties {

    /**
     * 是否启用 Canal 同步
     */
    private boolean enable = false;

    /**
     * Canal Server 地址
     */
    private String host = "127.0.0.1";

    /**
     * Canal Server 端口
     */
    private int port = 11111;

    /**
     * Canal destination
     */
    private String destination = "example";

    /**
     * Canal 用户名，可为空
     */
    private String username;

    /**
     * Canal 密码，可为空
     */
    private String password;

    /**
     * 每次拉取的批次大小
     */
    private int batchSize = 200;

    /**
     * 空批次轮询间隔
     */
    private long pollingIntervalMs = 500L;

    /**
     * Canal 订阅过滤
     */
    private String subscribeFilter = "network_disk\\.user_file";
}
