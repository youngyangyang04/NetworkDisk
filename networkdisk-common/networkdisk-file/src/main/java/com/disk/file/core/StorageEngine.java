package com.disk.file.core;

import com.disk.file.context.DeleteFileContext;
import com.disk.file.context.MergeFileContext;
import com.disk.file.context.ReadFileContext;
import com.disk.file.context.StoreFileChunkContext;
import com.disk.file.context.StoreFileContext;

import java.io.IOException;

/**
 * 类描述: 文件存储引擎
 *
 * @author weikunkun
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     *
     * @param context
     * @throws IOException
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     *
     * @param context
     * @throws IOException
     */
    void delete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     *
     * @param context
     * @throws IOException
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     *
     * @param context
     * @throws IOException
     */
    void mergeFile(MergeFileContext context) throws IOException;

    /**
     * 读取文件内容写入到输出流中
     *
     * @param context
     * @throws IOException
     */
    void realFile(ReadFileContext context) throws IOException;
}
