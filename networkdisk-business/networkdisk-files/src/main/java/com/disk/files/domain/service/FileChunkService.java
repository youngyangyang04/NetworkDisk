package com.disk.files.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.files.domain.context.FileChunkSaveContext;
import com.disk.files.domain.entity.FileChunkDO;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface FileChunkService extends IService<FileChunkDO> {

    /**
     * 文件分片保存
     *
     * @param context
     */
    void saveChunkFile(FileChunkSaveContext context);
}
