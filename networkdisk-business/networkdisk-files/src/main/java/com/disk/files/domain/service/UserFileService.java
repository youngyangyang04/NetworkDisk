package com.disk.files.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.files.domain.context.CopyFileContext;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.FileChunkMergeContext;
import com.disk.files.domain.context.FileChunkUploadContext;
import com.disk.files.domain.context.FileDownloadContext;
import com.disk.files.domain.context.FilePreviewContext;
import com.disk.files.domain.context.FileSearchContext;
import com.disk.files.domain.context.QueryBreadcrumbsContext;
import com.disk.files.domain.context.QueryFolderTreeContext;
import com.disk.files.domain.context.QueryUploadedChunksContext;
import com.disk.files.domain.context.TransferFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.response.BreadcrumbVO;
import com.disk.files.domain.response.FileSearchVO;
import com.disk.files.domain.response.FolderTreeNodeVO;
import com.disk.files.infrastructure.enums.FolderFlagEnum;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface UserFileService extends IService<UserFileDO> {

    /**
     * 获取文件用户文件列表
     * @param context
     * @return
     */
    List<UserFileDO> getUserFileList(QueryFileContext context);

    /**
     * 创建文件夹
     * @param context
     * @return
     */
    Long createFolder(CreateFolderContext context);

    /**
     * 更新文件名
     * @param context
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 文件删除
     * @param context
     */
    void deleteFile(DeleteUserFileContext context);

    /**
     * 文件秒传
     * @param context
     * @return
     */
    boolean secUpload(SecUploadFileContext context);

    /**
     * 文件上传
     * @param context
     */
    void upload(UploadFileContext context);

    /**
     * @param context
     * @return
     */
    Integer chunkUpload(FileChunkUploadContext context);

    /**
     *
     * @param context
     * @return
     */
    List<Integer> getUploadedChunkList(QueryUploadedChunksContext context);

    /**
     * 文件合并
     * @param context
     */
    void mergeFile(FileChunkMergeContext context);

    /**
     * 文件下载
     * @param context
     */
    void download(FileDownloadContext context);

    /**
     * 文件预览
     * @param context
     */
    void preview(FilePreviewContext context);

    /**
     * 查询文件夹树
     * @param context
     * @return
     */
    List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context);

    /**
     * 文件移动
     * @param context
     */
    void transfer(TransferFileContext context);

    /**
     * 文件复制
     * @param context
     */
    void copy(CopyFileContext context);

    /**
     * 文件搜搜
     * @param context
     * @return
     */
    List<FileSearchVO> search(FileSearchContext context);

    /**
     * 获取用户根文件信息
     * @param userId
     * @param folderFlag
     * @return
     */
    UserFileDO getUserRootInfo(Long userId, FolderFlagEnum folderFlag);

    /**
     * 获取
     * @param context
     * @return
     */
    List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context);
}
