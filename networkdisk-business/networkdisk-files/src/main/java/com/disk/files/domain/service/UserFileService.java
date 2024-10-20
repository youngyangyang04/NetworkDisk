package com.disk.files.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.UserFileDO;

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
}
