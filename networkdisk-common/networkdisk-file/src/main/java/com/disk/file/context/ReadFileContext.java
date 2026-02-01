package com.disk.file.context;

import lombok.Getter;
import lombok.Setter;

import java.io.OutputStream;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class ReadFileContext {

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的输出流
     */
    private OutputStream outputStream;
}
