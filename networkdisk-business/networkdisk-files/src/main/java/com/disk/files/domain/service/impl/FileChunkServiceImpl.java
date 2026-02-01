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

import java.io.IOException;
import java.util.Date;

/**
 * 类描述: TODO
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
     * 分配保存
     * @param context
     */
    @Override
    public void saveChunkFile(FileChunkSaveContext context) {
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    /**
     * 判断是否所有的分片均没上传完成
     *
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        long count = count(queryWrapper);
        if (count == context.getTotalChunks().longValue()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 执行文件分片上传保存的操作
     * <p>
     * 1、委托文件存储引擎存储文件分片
     * 2、保存文件分片记录
     *
     * @param context
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        doStoreFileChunk(context);
        doSaveRecord(context);
    }

    /**
     * 保存文件分片记录
     *
     * @param context
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
     * 委托文件存储引擎保存文件分片
     *
     * @param context
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
