package com.disk.files.domain.context;

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
public class StoreFileContext {

    /**
     * 上传的文件名称
     */
    private String filename;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件的输入流信息
     */
    private InputStream inputStream;

    /**
     * 文件上传后的物理路径
     */
    private String realPath;
}
