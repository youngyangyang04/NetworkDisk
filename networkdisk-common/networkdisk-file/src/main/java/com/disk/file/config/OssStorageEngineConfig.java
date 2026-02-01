package com.disk.file.config;

import com.aliyun.oss.OSSClient;
import com.disk.base.exception.SystemException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.disk.file.storage.engine.oss")
public class OssStorageEngineConfig {

    /**
     * 断端点
     */
    private String endpoint;

    /**
     * ak
     */
    private String accessKeyId;

    /**
     * sk
     */
    private String accessKeySecret;

    /**
     * bucket name
     */
    private String bucketName;

    /**
     * 是否自动创建bucket
     */
    private Boolean autoCreateBucket = Boolean.TRUE;

    /**
     * 注入OSS操作客户端对象
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient() {
        if (StringUtils.isAnyBlank(getEndpoint(), getAccessKeyId(), getAccessKeySecret(), getBucketName())) {
            throw new SystemException("the oss config is missed!");
        }
        return new OSSClient(getEndpoint(), getAccessKeyId(), getAccessKeySecret());
    }
}
