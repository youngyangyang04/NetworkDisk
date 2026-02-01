//package com.disk.delayqueue.config;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.ClusterServersConfig;
//import org.redisson.config.Config;
//import org.redisson.config.SentinelServersConfig;
//import org.redisson.config.SingleServerConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//
///**
// * 类描述: 数据源配置
// *
// * @author weikunkun
// */
//@Configuration
//@ConditionalOnProperty(prefix = "spring.redis")
//public class RedissonConfiguration {
//
//    @Autowired
//    private RedisProperties redisProperties;
//
//    /**
//     * redisson协议前缀
//     */
//    private static final String SCHEMA_PREFIX = "redis://";
//
//    @Bean("redissonClient")
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
//        RedisProperties.Cluster redisPropertiesCluster = redisProperties.getCluster();
//        if (redisPropertiesCluster != null) {
//            //集群redis
//            ClusterServersConfig clusterServersConfig = config.useClusterServers();
//            for (String cluster : redisPropertiesCluster.getNodes()) {
//                clusterServersConfig.addNodeAddress(SCHEMA_PREFIX + cluster);
//            }
//            if (StringUtils.hasText(redisProperties.getPassword())) {
//                clusterServersConfig.setPassword(redisProperties.getPassword());
//            }
//            clusterServersConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
//            clusterServersConfig.setPingConnectionInterval(30000);
//        } else if (StringUtils.hasText(redisProperties.getHost())) {
//            //单点redis
//            SingleServerConfig singleServerConfig = config.useSingleServer().
//                    setAddress(SCHEMA_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort());
//            if (StringUtils.hasText(redisProperties.getPassword())) {
//                singleServerConfig.setPassword(redisProperties.getPassword());
//            }
//            singleServerConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
//            singleServerConfig.setPingConnectionInterval(30000);
//            singleServerConfig.setDatabase(redisProperties.getDatabase());
//        } else if (sentinel != null) {
//            //哨兵模式
//            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
//            sentinelServersConfig.setMasterName(sentinel.getMaster());
//            for (String node : sentinel.getNodes()) {
//                sentinelServersConfig.addSentinelAddress(SCHEMA_PREFIX + node);
//            }
//            if (StringUtils.hasText(redisProperties.getPassword())) {
//                sentinelServersConfig.setPassword(redisProperties.getPassword());
//            }
//            sentinelServersConfig.setTimeout((int) redisProperties.getTimeout().toMillis());
//            sentinelServersConfig.setPingConnectionInterval(30000);
//            sentinelServersConfig.setDatabase(redisProperties.getDatabase());
//        }
//        return Redisson.create(config);
//    }
//}
