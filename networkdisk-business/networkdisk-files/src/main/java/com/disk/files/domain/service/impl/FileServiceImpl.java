package com.disk.files.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.exception.BizException;
import com.disk.base.utils.IdUtil;
import com.disk.files.domain.context.DeleteFileContext;
import com.disk.files.domain.context.ListFileContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.StoreFileContext;
import com.disk.files.domain.entity.FileDO;
import com.disk.files.domain.service.FileService;
import com.disk.files.infrastructure.mapper.FileMapper;
import com.disk.base.utils.FileUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileDO> implements FileService {


    @Override
    public List<FileDO> getFileList(ListFileContext context) {
        return List.of();
    }

    @Override
    public void saveFile(SaveFileContext context) {
        storeMultipartFile(context);
        FileDO record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setFileRecord(record);
    }

    /**
     * 保存实体文件记录
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private FileDO doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        FileDO record = assembleRPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                // TODO 存储引擎删除文件信息
            } catch (Exception e) {
                e.printStackTrace();
                // TODO 发送事件
            }
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private FileDO assembleRPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        FileDO record = new FileDO();
        record.setId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtil.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtil.getFileSuffix(filename));
        record.setFilePreviewContentType(FileUtil.getContentType(realPath));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        return record;
    }

    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     *
     * @param context
     */
    private void storeMultipartFile(SaveFileContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            // TODO 通过文件引擎存储文件信息
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }
}
