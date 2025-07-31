package com.disk.files.domain.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileChunkUploadContext {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 总体的分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片下标
     * 从1开始
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}