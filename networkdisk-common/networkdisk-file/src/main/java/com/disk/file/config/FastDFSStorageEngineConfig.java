package com.disk.file.config;

import com.disk.base.exception.SystemException;
import com.github.tobato.fastdfs.conn.ConnectionPoolConfig;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@SpringBootConfiguration
@Data
@ConfigurationProperties(prefix = "com.disk.file.storage.engine.fdfs")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan(value = {"com.github.tobato.fastdfs.service", "com.github.tobato.fastdfs.domain"})
public class FastDFSStorageEngineConfig {

    /**
     * 连接的超时时间
     */
    private Integer connectTimeout = 600;

    /**
     * 跟踪服务器地址列表
     */
    private List<String> trackerList = Lists.newArrayList();

    /**
     * 组名称
     */
    private String group = "group1";

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectTimeout(getConnectTimeout());
        return factory;
    }

    @Bean
    public ConnectionPoolConfig connectionPoolConfig() {
        return new ConnectionPoolConfig();
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPool(ConnectionPoolConfig connectionPoolConfig, PooledConnectionFactory factory) {
        FdfsConnectionPool fdfsConnectionPool = new FdfsConnectionPool(factory, connectionPoolConfig);
        return fdfsConnectionPool;
    }

    @Bean
    public TrackerConnectionManager trackerConnectionManager(FdfsConnectionPool fdfsConnectionPool) {
        TrackerConnectionManager manager = new TrackerConnectionManager(fdfsConnectionPool);
        if (CollectionUtils.isEmpty(getTrackerList())) {
            throw new SystemException("the tracker list is empty!");
        }
        manager.setTrackerList(getTrackerList());
        return manager;
    }
}
