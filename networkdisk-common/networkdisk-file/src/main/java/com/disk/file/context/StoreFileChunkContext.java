package com.disk.file.context;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class StoreFileChunkContext {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的下标
     */
    private Integer chunkNumber;

    /*
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 当前登录用户的ID
     */
    private Long userId;
}
