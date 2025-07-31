package com.disk.files.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.files.domain.context.FileChunkMergeAndSaveContext;
import com.disk.files.domain.context.ListFileContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.entity.FileDO;

import java.util.List;

/**
 * 类描述: 文件服务
 *
 * @author weikunkun
 */
public interface FileService extends IService<FileDO> {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    List<FileDO> getFileList(ListFileContext context);

    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     */
    void saveFile(SaveFileContext context);

    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext);
}
