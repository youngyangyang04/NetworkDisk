package com.disk.files.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.constant.BaseConstant;
import com.disk.base.enums.DeleteEnum;
import com.disk.base.exception.BizException;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.IdUtil;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.FileDO;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.entity.convertor.FileConvertor;
import com.disk.files.domain.service.FileService;
import com.disk.files.domain.service.UserFileService;
import com.disk.files.exception.FileException;
import com.disk.files.exception.FilesErrorCode;
import com.disk.files.infrastructure.constant.FileConstant;
import com.disk.files.infrastructure.enums.FileTypeEnum;
import com.disk.files.infrastructure.enums.FolderFlagEnum;
import com.disk.files.infrastructure.mapper.UserFileMapper;
import com.disk.base.utils.FileUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.disk.files.exception.FilesErrorCode.FILE_DELETE_ERROR;
import static com.disk.files.exception.FilesErrorCode.FILE_NEW_NAME_EQUALS;
import static com.disk.files.exception.FilesErrorCode.FILE_NEW_NAME_EXIST;
import static com.disk.files.exception.FilesErrorCode.FILE_NOT_CUR_USER;
import static com.disk.files.exception.FilesErrorCode.FILE_RENAME_ERROR;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFileDO> implements UserFileService {

    @Autowired
    private FileConvertor fileConvertor;

    @Autowired
    private FileService fileService;


    @Override
    public List<UserFileDO> getUserFileList(QueryFileContext context) {
        return baseMapper.listUserFiles(context);
    }

    @Override
    public Long createFolder(CreateFolderContext context) {
        return saveUserFile(context.getParentId(),
                context.getFolderName(),
                FolderFlagEnum.YES,
                null,
                null,
                context.getUserId(),
                null);
    }

    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    @Override
    public void deleteFile(DeleteUserFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        // 根据文件ID列表查询文件记录
        // 如果查询到的文件记录数量与文件ID列表数量不一致，则抛出异常，表示存在不合法的文件记录。
        List<UserFileDO> userFiles = listByIds(fileIdList);
        if (userFiles.size() != fileIdList.size()) {
            throw new FileException(FILE_DELETE_ERROR);
        }

        // 检查文件ID集合是否有重复id
        Set<Long> fileIdSet = userFiles.stream().map(UserFileDO::getId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();

        if (oldSize != newSize) {
            throw new FileException(FILE_DELETE_ERROR);
        }

        // 检查文件id所属用户是否唯一
        Set<Long> userIdSet = userFiles.stream().map(UserFileDO::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new FileException(FILE_DELETE_ERROR);
        }

        // 检查文件是否属于该用户
        Long dbUserId = userIdSet.stream().findFirst().get();
        if (!Objects.equals(dbUserId, context.getUserId())) {
            throw new FileException(FILE_DELETE_ERROR);
        }
        doDeleteFile(context);
        // TODO 发布删除事件
        afterFileDelete(context);
    }

    @Override
    public boolean secUpload(SecUploadFileContext context) {
        List<FileDO> fileList = getFileListByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());
        if (CollectionUtils.isNotEmpty(fileList)) {
            FileDO record = fileList.get(BaseConstant.ZERO_INT);
            saveUserFile(context.getParentId(),
                    context.getFilename(),
                    FolderFlagEnum.NO,
                    FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                    record.getId(),
                    context.getUserId(),
                    record.getFileSizeDesc());
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(UploadFileContext context) {
        saveFile(context);
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                context.getFileRecord().getId(),
                context.getUserId(),
                context.getFileRecord().getFileSizeDesc());
    }

    /**
     * 文件保存
     * @param context
     */
    private void saveFile(UploadFileContext context) {
        SaveFileContext fileSaveContext = fileConvertor.fileUploadContextToFileSaveContext(context);
        fileService.saveFile(fileSaveContext);
        context.setFileRecord(fileSaveContext.getFileRecord());
    }

    /**
     * 查询文件列表
     * @param userId
     * @param identifier
     * @return
     */
    private List<FileDO> getFileListByUserIdAndIdentifier(Long userId, String identifier) {
        return Lists.newArrayList();
    }

    /**
     * 执行文件删除的操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteUserFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.in("file_id", fileIdList);
        updateWrapper.set("deleted", DeleteEnum.YES.getCode());

        if (!update(updateWrapper)) {
            throw new FileException(FILE_DELETE_ERROR);
        }
    }

    /**
     * 文件删除的后置操作
     * 1、对外发布文件删除的事件
     *  - 文件被删除之后，刷新所有受影响的分享的状态
     *
     * @param context
     */
    private void afterFileDelete(DeleteUserFileContext context) {
        // TODO
    }

    /**
     * 执行文件重命名的操作
     *
     * @param context
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        UserFileDO entity = context.getEntity();
        entity.setFilename(context.getNewFilename());

        if (!updateById(entity)) {
            throw new FileException(FILE_RENAME_ERROR);
        }
    }

    /**
     * 更新文件名称的条件校验
     * <p>
     * 1、文件ID是有效的
     * 2、用户有权限更新该文件的文件名称
     * 3、新旧文件名称不能一样
     * 4、不能使用当前文件夹下面的子文件的名称
     *
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {

        Long fileId = context.getFileId();
        UserFileDO entity = getById(fileId);

        if (Objects.isNull(entity)) {
            throw new FileException(FilesErrorCode.FILE_NOT_EXIT);
        }

        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new BizException("abc", FILE_NOT_CUR_USER);
        }

        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new BizException(FILE_NEW_NAME_EQUALS);
        }


        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        long count = count(queryWrapper);

        if (count > 0) {
            throw new BizException(FILE_NEW_NAME_EXIST);
        }
        context.setEntity(entity);
    }


    /**
     * 保存用户文件信息
     *
     * @param parentId
     * @param filename
     * @param folderFlagEnum
     * @param fileType       文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @return
     */
    private Long saveUserFile(Long parentId,
                              String filename,
                              FolderFlagEnum folderFlagEnum,
                              Integer fileType,
                              Long realFileId,
                              Long userId,
                              String fileSizeDesc) {
        UserFileDO entity = assembleUserFile(parentId, userId, filename, folderFlagEnum, fileType, realFileId, fileSizeDesc);
        if (!save((entity))) {
            throw new SystemException("保存文件信息失败");
        }
        return entity.getId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1、构建并填充实体
     * 2、处理文件命名一致的问题
     *
     * @param parentId
     * @param userId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param fileSizeDesc
     * @return
     */
    private UserFileDO assembleUserFile(Long parentId, Long userId, String filename, FolderFlagEnum folderFlagEnum,
                                        Integer fileType, Long realFileId, String fileSizeDesc) {
        UserFileDO entity = new UserFileDO();

        entity.setId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFilename(filename);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDeleted(DeleteEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);

        handleDuplicateFilename(entity);
        return entity;
    }

    /**
     * 处理用户重复名称
     * 如果同一文件夹下面有文件名称重复
     * 按照系统级规则重命名文件
     *
     * @param entity
     */
    private void handleDuplicateFilename(UserFileDO entity) {
        String filename = entity.getFilename(), newFilenameWithoutSuffix, newFilenameSuffix;
        int newFilenamePointPosition = filename.lastIndexOf(BaseConstant.POINT_STR);
        if (newFilenamePointPosition == BaseConstant.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = StringUtils.EMPTY;
        } else {
            newFilenameWithoutSuffix = filename.substring(BaseConstant.ZERO_INT, newFilenamePointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StringUtils.EMPTY);
        }

        List<UserFileDO> existRecords = getDuplicateFilename(entity, newFilenameWithoutSuffix);

        if (CollectionUtils.isEmpty(existRecords)) {
            return;
        }

        List<String> existFilenames = existRecords.stream().map(UserFileDO::getFilename).collect(Collectors.toList());

        int count = 1;
        String newFilename;

        do {
            newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
            count++;
        } while (existFilenames.contains(newFilename));

        entity.setFilename(newFilename);
    }

    /**
     * 查找通易付文件夹下面的同名文件数量
     *
     * @param entity
     * @param newFilenameWithoutSuffix
     * @return
     */
    private List<UserFileDO> getDuplicateFilename(UserFileDO entity, String newFilenameWithoutSuffix) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("folder_flag", entity.getFolderFlag());
        queryWrapper.eq("user_id", entity.getUserId());
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        queryWrapper.likeRight("filename", newFilenameWithoutSuffix);
        return list(queryWrapper);
    }

    /**
     * 拼装新文件名称
     * 拼装规则参考操作系统重复文件名称的重命名规范
     *
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @return
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        String newFilename = new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConstant.CN_LEFT_PARENTHESES_STR)
                .append(count)
                .append(FileConstant.CN_RIGHT_PARENTHESES_STR)
                .append(newFilenameSuffix)
                .toString();
        return newFilename;
    }
}
