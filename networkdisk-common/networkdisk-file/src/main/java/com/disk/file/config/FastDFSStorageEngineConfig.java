package com.disk.file.config;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.disk.file.storage.engine.fdfs")
@ComponentScan(value = {"com.github.tobato.fastdfs.service", "com.github.tobato.fastdfs.domain"})
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
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


    /**
     * fastdfs对外域名
     */
    private String outUrl;
}
