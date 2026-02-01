package com.disk.files.infrastructure.config;

import com.disk.base.constant.BaseConstant;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.disk.file")
public class DiskConfig {

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = BaseConstant.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:8080/share/";
}
