package com.disk.file.core;

import cn.hutool.core.lang.Assert;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.EmptyUtil;
import com.disk.cache.constant.CacheConstant;
import com.disk.file.context.DeleteFileContext;
import com.disk.file.context.MergeFileContext;
import com.disk.file.context.ReadFileContext;
import com.disk.file.context.StoreFileChunkContext;
import com.disk.file.context.StoreFileContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.util.Objects;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public abstract class AbstractStorageEngine implements StorageEngine {


    @Autowired
    private CacheManager cacheManager;

    protected Cache getCache() {
        if (EmptyUtil.isEmpty(cacheManager)) {
            throw new SystemException("具体的缓存实现需要引用到项目中");
        }
        return cacheManager.getCache(CacheConstant.CACHE_COMMON_NAME);
    }

    /**
     * 存储物理文件
     * <p>
     * 1、参数校验
     * 2、执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 校验上传物理文件的上下文信息
     *
     * @param context
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件的总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

    /**
     * 删除物理文件
     * <p>
     * 1、参数校验
     * 2、执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }

    /**
     * 执行删除物理文件的动作
     * 下沉到子类去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    /**
     * 校验删除物理文件的上下文信息
     *
     * @param context
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }

    /**
     * 存储物理文件的分片
     * <p>
     * 1、参数校验
     * 2、执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        checkStoreFileChunkContext(context);
        doStoreChunk(context);
    }

    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 校验保存文件分片的参数
     *
     * @param context
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getTotalSize(), "文件大小不能为空");
        Assert.notNull(context.getInputStream(), "文件分片不能为空");
        Assert.notNull(context.getTotalChunks(), "文件分片总数不能为空");
        Assert.notNull(context.getChunkNumber(), "文件分片下标不能为空");
        Assert.notNull(context.getCurrentChunkSize(), "文件分片的大小不能为空");
        Assert.notNull(context.getUserId(), "当前登录用户的ID不能为空");
    }

    /**
     * 合并文件分片
     * <p>
     * 1、检查参数
     * 2、执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        checkMergeFileContext(context);
        doMergeFile(context);
    }

    /**
     * 执行文件分片的动作
     * 下沉到子类实现
     *
     * @param context
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 检查文件分片合并的上线文实体信息
     *
     * @param context
     */
    private void checkMergeFileContext(MergeFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getUserId(), "当前登录用户的ID不能为空");
        Assert.notEmpty(context.getRealPathList(), "文件分片列表不能为空");
    }

    /**
     * 读取文件内容写入到输出流中
     * <p>
     * 1、参数校验
     * 2、执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void realFile(ReadFileContext context) throws IOException {
        checkReadFileContext(context);
        doReadFile(context);
    }

    /**
     * 读取文件内容并写入到输出流中
     * 下沉到子类去实现
     *
     * @param context
     */
    protected abstract void doReadFile(ReadFileContext context) throws IOException;

    /**
     * 文件读取参数校验
     *
     * @param context
     */
    private void checkReadFileContext(ReadFileContext context) {
        Assert.notBlank(context.getRealPath(), "文件真实存储路径不能为空");
        Assert.notNull(context.getOutputStream(), "文件的输出流不能为空");
    }

}
