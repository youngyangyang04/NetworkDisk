package com.disk.file.oss.initializer;

import com.aliyun.oss.OSSClient;
import com.disk.base.exception.SystemException;
import com.disk.file.config.OssStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@Component
public class OssBucketInitializer implements CommandLineRunner {

    @Autowired
    private OssStorageEngineConfig config;

    @Autowired
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(config.getBucketName());

        if (!bucketExist && config.getAutoCreateBucket()) {
            client.createBucket(config.getBucketName());
        }

        if (!bucketExist && !config.getAutoCreateBucket()) {
            throw new SystemException("the bucket " + config.getBucketName() + " is not available");
        }

        log.info("the bucket " + config.getBucketName() + " have been created!");
    }
}
