package com.disk.files.domain.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.IdUtil;
import com.disk.file.context.StoreFileChunkContext;
import com.disk.file.core.StorageEngine;
import com.disk.files.domain.context.FileChunkSaveContext;
import com.disk.files.domain.entity.FileChunkDO;
import com.disk.files.domain.entity.convertor.FileConvertor;
import com.disk.files.domain.service.FileChunkService;
import com.disk.files.infrastructure.config.DiskConfig;
import com.disk.files.infrastructure.enums.MergeFlagEnum;
import com.disk.files.infrastructure.mapper.FileChunkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 文件分片服务实现
 *
 * @author weikunkun
 */
@Service
public class FileChunkServiceImpl extends ServiceImpl<FileChunkMapper, FileChunkDO> implements FileChunkService {

    @Autowired
    private FileConvertor fileConvertor;

    @Autowired
    private StorageEngine storageEngine;

    @Autowired
    private DiskConfig config;

    /**
     * 分片保存。
     * 先清理唯一键下无效记录，再判断是否已经上传，最后再执行保存。
     *
     * @param context 分片上下文
     */
    @Override
    public void saveChunkFile(FileChunkSaveContext context) {
        clearInvalidChunkRecord(context);
        if (checkChunkUploaded(context)) {
            doJudgeMergeFile(context);
            return;
        }
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    @Override
    public void removeChunkRecordsPhysically(List<Long> chunkRecordIds) {
        if (CollectionUtils.isEmpty(chunkRecordIds)) {
            return;
        }
        baseMapper.deleteByIdsPhysical(chunkRecordIds);
    }

    /**
     * 判断是否所有分片均已上传完成。
     *
     * @param context 分片上下文
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        QueryWrapper<FileChunkDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.gt("expiration_time", new Date());
        long count = count(queryWrapper);
        if (count == context.getTotalChunks().longValue()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 执行文件分片上传保存。
     *
     * @param context 分片上下文
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        doStoreFileChunk(context);
        doSaveRecord(context);
    }

    /**
     * 通过唯一键查询分片是否已上传。
     *
     * @param context 分片上下文
     * @return true 已上传
     */
    private boolean checkChunkUploaded(FileChunkSaveContext context) {
        QueryWrapper<FileChunkDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("chunk_number", context.getChunkNumber());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.gt("expiration_time", new Date());
        return count(queryWrapper) > 0L;
    }

    /**
     * 清理同一分片唯一键下的失效记录（逻辑删除或已过期）。
     *
     * @param context 分片上下文
     */
    private void clearInvalidChunkRecord(FileChunkSaveContext context) {
        baseMapper.deleteDeletedOrExpiredByUniqueKey(
                context.getIdentifier(),
                context.getChunkNumber(),
                context.getUserId(),
                new Date()
        );
    }

    /**
     * 保存分片记录。
     *
     * @param context 分片上下文
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        FileChunkDO record = new FileChunkDO();
        record.setId(IdUtil.get());
        record.setIdentifier(context.getIdentifier());
        record.setRealPath(context.getRealPath());
        record.setChunkNumber(context.getChunkNumber());
        record.setExpirationTime(DateUtil.offsetDay(new Date(), config.getChunkFileExpirationDays()));
        record.setCreateUser(context.getUserId());
        record.setUpdateUser(context.getUserId());
        if (!save(record)) {
            throw new SystemException("文件分片上传失败");
        }
    }

    /**
     * 委托存储引擎保存分片到存储介质。
     *
     * @param context 分片上下文
     */
    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConvertor.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException("文件分片上传失败");
        }
    }
}
