package com.disk.file.fastdfs;

import com.disk.base.constant.BaseConstant;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.FileUtil;
import com.disk.file.config.FastDFSStorageEngineConfig;
import com.disk.file.context.DeleteFileContext;
import com.disk.file.context.MergeFileContext;
import com.disk.file.context.ReadFileContext;
import com.disk.file.context.StoreFileChunkContext;
import com.disk.file.context.StoreFileContext;
import com.disk.file.core.AbstractStorageEngine;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FastDFSStorageEngineConfig config;


    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        StorePath storePath = client.uploadFile(config.getGroup(), context.getInputStream(), context.getTotalSize(), FileUtil.getFileExtName(context.getFilename()));
        context.setRealPath(storePath.getFullPath());    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        List<String> realFilePathList = context.getRealFilePathList();
        if (CollectionUtils.isNotEmpty(realFilePathList)) {
            realFilePathList.forEach(client::deleteFile);
        }
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        throw new SystemException("FastDFS不支持分片上传");
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        throw new SystemException("FastDFS不支持分片上传");
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        String realPath = context.getRealPath();
        String group = realPath.substring(BaseConstant.ZERO_INT, realPath.indexOf(BaseConstant.SLASH_STR));
        String path = realPath.substring(realPath.indexOf(BaseConstant.SLASH_STR) + BaseConstant.ONE_INT);

        try (OutputStream outputStream = context.getOutputStream()) {
            DownloadByteArray downloadByteArray = new DownloadByteArray();
            byte[] bytes = client.downloadFile(group, path, downloadByteArray);
            outputStream.write(bytes);
            outputStream.flush();
        }

    }
}
