package com.disk.files.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.exception.BizException;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.IdUtil;
import com.disk.file.context.MergeFileContext;
import com.disk.file.core.StorageEngine;
import com.disk.files.domain.context.DeleteFileContext;
import com.disk.files.domain.context.FileChunkMergeAndSaveContext;
import com.disk.files.domain.context.ListFileContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.StoreFileContext;
import com.disk.files.domain.entity.FileChunkDO;
import com.disk.files.domain.entity.FileDO;
import com.disk.files.domain.service.FileChunkService;
import com.disk.files.domain.service.FileService;
import com.disk.files.infrastructure.mapper.FileMapper;
import com.disk.base.utils.FileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileDO> implements FileService {


    @Autowired
    private StorageEngine storageEngine;


    @Autowired
    private FileChunkService fileChunkService;

    @Override
    public List<FileDO> getFileList(ListFileContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        LambdaQueryWrapper<FileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), FileDO::getCreateUser, userId);
        queryWrapper.eq(StringUtils.isNotBlank(identifier), FileDO::getIdentifier, identifier);
        return list(queryWrapper);
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
     * 合并物理文件并保存物理文件记录
     * <p>
     * 1、委托文件存储引擎合并文件分片
     * 2、保存物理文件记录
     *
     * @param context
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        doMergeFileChunk(context);
        FileDO record = doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(), context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }

    /**
     * 委托文件存储引擎合并文件分片
     * <p>
     * 1、查询文件分片的记录
     * 2、根据文件分片的记录去合并物理文件
     * 3、删除文件分片记录
     * 4、封装合并文件的真实存储路径到上下文信息中
     *
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeAndSaveContext context) {
        QueryWrapper<FileChunkDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.ge("expiration_time", new Date());
        List<FileChunkDO> chunkRecoredList = fileChunkService.list(queryWrapper);
        if (CollectionUtils.isEmpty(chunkRecoredList)) {
            throw new SystemException("该文件未找到分片记录");
        }
        List<String> realPathList = chunkRecoredList.stream()
                .sorted(Comparator.comparing(FileChunkDO::getChunkNumber))
                .map(FileChunkDO::getRealPath)
                .collect(Collectors.toList());

        try {
            MergeFileContext mergeFileContext = new MergeFileContext();
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            storageEngine.mergeFile(mergeFileContext);
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException("文件分片合并失败");
        }

        List<Long> fileChunkRecordIdList = chunkRecoredList.stream().map(FileChunkDO::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(fileChunkRecordIdList);
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
        FileDO record = assembleFileDO(filename, realPath, totalSize, identifier, userId);
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
    private FileDO assembleFileDO(String filename, String realPath, Long totalSize, String identifier, Long userId) {
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
