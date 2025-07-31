package com.disk.files.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.constant.BaseConstant;
import com.disk.base.enums.DeleteEnum;
import com.disk.base.exception.SystemException;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.HttpUtil;
import com.disk.base.utils.IdUtil;
import com.disk.file.context.ReadFileContext;
import com.disk.file.core.StorageEngine;
import com.disk.files.domain.context.CopyFileContext;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.FileChunkMergeAndSaveContext;
import com.disk.files.domain.context.FileChunkMergeContext;
import com.disk.files.domain.context.FileChunkSaveContext;
import com.disk.files.domain.context.FileChunkUploadContext;
import com.disk.files.domain.context.FileDownloadContext;
import com.disk.files.domain.context.FilePreviewContext;
import com.disk.files.domain.context.FileSearchContext;
import com.disk.files.domain.context.ListFileContext;
import com.disk.files.domain.context.QueryBreadcrumbsContext;
import com.disk.files.domain.context.QueryFolderTreeContext;
import com.disk.files.domain.context.QueryUploadedChunksContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.TransferFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.FileDO;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.entity.convertor.FileConvertor;
import com.disk.files.domain.response.BreadcrumbVO;
import com.disk.files.domain.response.FileSearchVO;
import com.disk.files.domain.response.FolderTreeNodeVO;
import com.disk.files.domain.service.FileChunkService;
import com.disk.files.domain.service.FileService;
import com.disk.files.domain.service.UserFileService;
import com.disk.files.exception.FileException;
import com.disk.files.infrastructure.constant.FileConstant;
import com.disk.files.infrastructure.enums.FileTypeEnum;
import com.disk.files.infrastructure.enums.FolderFlagEnum;
import com.disk.files.infrastructure.mapper.UserFileMapper;
import com.disk.base.utils.FileUtil;
import com.disk.lock.DistributeLock;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.disk.files.exception.FilesErrorCode.FILE_DELETE_ERROR;
import static com.disk.files.exception.FilesErrorCode.FILE_NEW_NAME_EQUALS;
import static com.disk.files.exception.FilesErrorCode.FILE_NEW_NAME_EXIST;
import static com.disk.files.exception.FilesErrorCode.FILE_NOT_CUR_USER;
import static com.disk.files.exception.FilesErrorCode.FILE_NOT_EXIT;
import static com.disk.files.exception.FilesErrorCode.FILE_NO_AUTH;
import static com.disk.files.exception.FilesErrorCode.FILE_RENAME_ERROR;
import static com.disk.files.exception.FilesErrorCode.FOLDER_INVALID;
import static com.disk.files.exception.FilesErrorCode.FOLDER_NOT_DOWNLOAD;
import static com.disk.files.exception.FilesErrorCode.TARGET_FOLDER_TYPE_ERROR;

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

    @Autowired
    private FileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;
    @Autowired
    private UserFileMapper userFileMapper;


    @Override
    public List<UserFileDO> getUserFileList(QueryFileContext context) {
        return baseMapper.listUserFiles(context);
    }

    @Override
    public Long createFolder(CreateFolderContext context) {
        UserFileDO entity = assembleUserFolder(context);
        if (!save((entity))) {
            throw new SystemException("保存文件信息失败");
        }
        return entity.getId();
    }

    /**
     * 组装文件夹信息
     * @param context
     * @return
     */
    private UserFileDO assembleUserFolder(CreateFolderContext context) {
        UserFileDO entity = new UserFileDO();

        entity.setId(IdUtil.get());
        entity.setUserId(context.getUserId());
        entity.setParentId(context.getParentId());
        entity.setRealFileId(null);
        entity.setFilename(context.getFolderName());
        entity.setFolderFlag(FolderFlagEnum.YES.getCode());
        entity.setFileSizeDesc(null);
        entity.setFileType(null);
        entity.setDeleted(DeleteEnum.NO.getCode());
        entity.setCreateUser(context.getUserId());
        entity.setUpdateUser(context.getUserId());

        handleDuplicateFilename(entity);
        return entity;
    }

    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);

        UserFileDO entity = context.getEntity();
        entity.setFilename(context.getNewFilename());

        if (!updateById(entity)) {
            throw new FileException(FILE_RENAME_ERROR);
        }
    }

    @Override
    public void deleteFile(DeleteUserFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        // 根据文件ID列表查询文件记录
        List<UserFileDO> userFiles = listByIds(fileIdList);

        // 检查文件id所属用户是否唯一
        Set<Long> userIdSet = userFiles.stream().map(UserFileDO::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new FileException(FILE_DELETE_ERROR);
        }

        // 检查文件是否属于该用户
        Long creatUser = userIdSet.stream().findFirst().get();
        if (!Objects.equals(creatUser, context.getUserId())) {
            throw new FileException(FILE_DELETE_ERROR);
        }
        doDeleteFile(context);
        // TODO 发布删除事件
    }

    @Override
    public boolean secUpload(SecUploadFileContext context) {
        List<FileDO> fileList = getFilesByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());
        if (EmptyUtil.isEmpty(fileList)) {
            return false;
        }
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
     * 文件分片上传
     * <p>
     * 1、上传实体文件
     * 2、保存分片文件记录
     * 3、校验是否全部分片上传完成
     *
     * @param context
     * @return
     */
    @DistributeLock(scene = "FILE_CHUNK_UPLOAD", keyExpression = "#context.userId + '-' + #context.identifier")
    @Override
    public Integer chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = fileConvertor.fileChunkUploadContextToFileChunkSaveContext(context);
        fileChunkService.saveChunkFile(fileChunkSaveContext);
        return fileChunkSaveContext.getMergeFlagEnum().getCode();
    }

    /**
     * 查询用户已上传的分片列表
     * <p>
     * 1、查询已上传的分片列表
     * 2、封装返回实体
     *
     * @param context
     * @return
     */
    @Override
    public List<Integer> getUploadedChunkList(QueryUploadedChunksContext context) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.select("chunk_number");
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.gt("expiration_time", new Date());

        List<Integer> uploadedChunks = fileChunkService.listObjs(queryWrapper, value -> (Integer) value);
        return uploadedChunks;
    }

    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileChunkAndSaveFile(context);
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                context.getRecord().getId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件下载
     * <p>
     * 1、参数校验：校验文件是否存在，文件是否属于该用户
     * 2、校验该文件是不是一个文件夹
     * 3、执行下载的动作
     *
     * @param context
     */
    @Override
    public void download(FileDownloadContext context) {
        UserFileDO record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (isFolder(record)) {
            throw new FileException(FOLDER_NOT_DOWNLOAD);
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件预览
     * 1、参数校验：校验文件是否存在，文件是否属于该用户
     * 2、校验该文件是不是一个文件夹
     * 3、执行预览的动作
     *
     * @param context
     */
    @Override
    public void preview(FilePreviewContext context) {
        UserFileDO record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (isFolder(record)) {
            throw new SystemException("文件夹暂不支持下载");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * <p>
     * 1、查询出该用户的所有文件夹列表
     * 2、在内存中拼装文件夹树
     *
     * @param context
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<UserFileDO> folderRecords = queryFolderRecords(context.getUserId());
        return assembleFolderTreeNodeVOList(folderRecords);
    }

    /**
     * 文件转移
     * <p>
     * 1、权限校验
     * 2、执行工作
     *
     * @param context
     */
    @Override
    public void transfer(TransferFileContext context) {
        checkTransferCondition(context);
        List<UserFileDO> prepareRecords = context.getPrepareRecords();
        prepareRecords.forEach(record -> {
            record.setParentId(context.getTargetParentId());
            record.setUserId(context.getUserId());
            record.setCreateUser(context.getUserId());
            record.setUpdateUser(context.getUserId());
            handleDuplicateFilename(record);
        });
        if (!updateBatchById(prepareRecords)) {
            throw new SystemException("文件转移失败");
        }
    }

    /**
     * 文件复制
     * <p>
     * 1、条件校验
     * 2、执行动作
     *
     * @param context
     */
    @Override
    public void copy(CopyFileContext context) {
        checkCopyCondition(context);
        List<UserFileDO> prepareRecords = context.getPrepareRecords();
        if (EmptyUtil.isEmpty(prepareRecords)) {
            return;
        }
        List<UserFileDO> allRecords = Lists.newArrayList();
        prepareRecords.forEach(record -> assembleCopyChildRecord(allRecords, record, context.getTargetParentId(), context.getUserId()));
        if (!saveBatch(allRecords)) {
            throw new SystemException("文件复制失败");
        }
    }

    @Override
    public List<FileSearchVO> search(FileSearchContext context) {
        List<FileSearchVO> result = doSearch(context);
        fillParentFilename(result);
        return result;
    }

    @Override
    public UserFileDO getUserRootInfo(Long userId, FolderFlagEnum folderFlag) {
        QueryFileContext context = new QueryFileContext();
        context.setUserId(userId);
        context.setFileFolderType(folderFlag.getCode());
        context.setParentId(FileConstant.TOP_PARENT_ID);
        UserFileDO userRootInfo = userFileMapper.getUserRootInfo(context);
        return userRootInfo;
    }

    @Override
    public List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context) {
        List<UserFileDO> folderRecords = queryFolderRecords(context.getUserId());
        Map<Long, BreadcrumbVO> prepareBreadcrumbVOMap = folderRecords.stream().map(BreadcrumbVO::transfer).collect(Collectors.toMap(BreadcrumbVO::getId, a -> a));
        BreadcrumbVO currentNode;
        Long fileId = context.getFileId();
        List<BreadcrumbVO> result = Lists.newLinkedList();
        do {
            currentNode = prepareBreadcrumbVOMap.get(fileId);
            if (Objects.nonNull(currentNode)) {
                result.add(0, currentNode);
                fileId = currentNode.getParentId();
            }
        } while (Objects.nonNull(currentNode));
        return result;
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
    private List<FileDO> getFilesByUserIdAndIdentifier(Long userId, String identifier) {
        ListFileContext context = new ListFileContext();
        context.setUserId(userId);
        context.setIdentifier(identifier);
        return fileService.getFileList(context);
    }

    /**
     * 执行文件删除的操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteUserFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.in("id", fileIdList);
        updateWrapper.set("deleted", DeleteEnum.YES.getCode());

        if (!update(updateWrapper)) {
            throw new FileException(FILE_DELETE_ERROR);
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

        if (EmptyUtil.isEmpty(entity)) {
            throw new FileException(FILE_NOT_EXIT);
        }

        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new FileException("abc", FILE_NOT_CUR_USER);
        }

        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new FileException(FILE_NEW_NAME_EQUALS);
        }


        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        long count = count(queryWrapper);

        if (count > 0) {
            throw new FileException(FILE_NEW_NAME_EXIST);
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
        if (!save((entity))) {
            throw new SystemException("保存文件信息失败");
        }
        return entity.getId();
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

    /**
     * 查询用户所有有效的文件夹信息
     *
     * @param userId
     * @return
     */
    private List<UserFileDO> queryFolderRecords(Long userId) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        return list(queryWrapper);
    }

    /**
     * 拼装文件夹树列表
     *
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<UserFileDO> folderRecords) {
        if (CollectionUtils.isEmpty(folderRecords)) {
            return Lists.newArrayList();
        }
        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream().map(fileConvertor::userFile2FolderTreeNodeVO).toList();
        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));
        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                node.getChildren().addAll(children);
            }
        }
        return mappedFolderTreeNodeVOList.stream().filter(node -> Objects.equals(node.getParentId(), FileConstant.TOP_PARENT_ID)).collect(Collectors.toList());
    }

    /**
     * 合并文件分片并保存物理文件记录
     *
     * @param context
     */
    private void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConvertor.fileChunkMergeContextToSaveContext(context);
        fileService.mergeFileChunkAndSaveFile(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 校验用户的操作权限
     * <p>
     * 1、文件记录必须存在
     * 2、文件记录的创建者必须是该登录用户
     *
     * @param record
     * @param userId
     */
    private void checkOperatePermission(UserFileDO record, Long userId) {
        if (EmptyUtil.isEmpty(record)) {
            throw new FileException(FILE_NOT_EXIT);
        }
        if (Objects.equals(record.getUserId(), userId)) {
            throw new FileException(FILE_NO_AUTH);
        }
    }

    /**
     * 检查当前文件记录是不是一个文件夹
     *
     * @param record
     * @return
     */
    private boolean isFolder(UserFileDO record) {
        if (EmptyUtil.isEmpty(record)) {
            throw new FileException(FILE_NOT_EXIT);
        }
        return Objects.equals(FolderFlagEnum.YES.getCode(), record.getFolderFlag());
    }

    /**
     * 执行文件下载的动作
     * <p>
     * 1、查询文件的真实存储路径
     * 2、添加跨域的公共响应头
     * 3、拼装下载文件的名称、长度等等响应信息
     * 4、委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doDownload(UserFileDO record, HttpServletResponse response) {
        FileDO fileRecord = fileService.getById(record.getRealFileId());
        if (EmptyUtil.isEmpty(fileRecord)) {
            throw new FileException(FILE_NOT_EXIT);
        }
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response, record, fileRecord);
        realFile2OutputStream(fileRecord.getRealPath(), response);
    }

    /**
     * 添加公共的文件读取响应头
     *
     * @param response
     * @param contentTypeValue
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader(FileConstant.CONTENT_TYPE_STR, contentTypeValue);
        response.setContentType(contentTypeValue);
    }

    /**
     * 添加文件下载的属性信息
     *
     * @param response
     * @param record
     * @param realFileRecord
     */
    private void addDownloadAttribute(HttpServletResponse response, UserFileDO record, FileDO realFileRecord) {
        try {
            response.addHeader(FileConstant.CONTENT_DISPOSITION_STR,
                    FileConstant.CONTENT_DISPOSITION_VALUE_PREFIX_STR + new String(record.getFilename().getBytes(FileConstant.GB2312_STR), FileConstant.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new SystemException(FILE_NOT_EXIT);
        }
        response.setContentLengthLong(Long.parseLong(realFileRecord.getFileSize()));
    }

    /**
     * 委托文件存储引擎去读取文件内容并写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void realFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext context = new ReadFileContext();
            context.setRealPath(realPath);
            context.setOutputStream(response.getOutputStream());
            storageEngine.realFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException("文件下载失败");
        }
    }

    /**
     * 执行文件预览的动作
     * 1、查询文件的真实存储路径
     * 2、添加跨域的公共响应头
     * 3、委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doPreview(UserFileDO record, HttpServletResponse response) {
        FileDO realFileRecord = fileService.getById(record.getRealFileId());
        if (EmptyUtil.isEmpty(realFileRecord)) {
            throw new SystemException("当前的文件记录不存在");
        }
        addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        realFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 文件转移的条件校验
     * <p>
     * 1、目标文件必须是一个文件夹
     * 2、选中的要转移的文件列表中不能含有目标文件夹以及其子文件夹
     *
     * @param context
     */
    private void checkTransferCondition(TransferFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!isFolder(getById(targetParentId))) {
            throw new FileException(TARGET_FOLDER_TYPE_ERROR);
        }
        List<Long> fileIdList = context.getFileIdList();
        List<UserFileDO> prepareRecords = listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new FileException(FILE_NOT_EXIT);
        }
    }

    /**
     * 校验目标文件夹ID是都是要操作的文件记录的文件夹ID以及其子文件夹ID
     * <p>
     * 1、如果要操作的文件列表中没有文件夹，那就直接返回false
     * 2、拼装文件夹ID以及所有子文件夹ID，判断存在即可
     *
     * @param prepareRecords
     * @param targetParentId
     * @param userId
     * @return
     */
    private boolean checkIsChildFolder(List<UserFileDO> prepareRecords, Long targetParentId, Long userId) {
        prepareRecords = prepareRecords.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(prepareRecords)) {
            return false;
        }
        List<UserFileDO> folderRecords = queryFolderRecords(userId);
        Map<Long, List<UserFileDO>> folderRecordMap = folderRecords.stream().collect(Collectors.groupingBy(UserFileDO::getParentId));
        List<UserFileDO> unavailableFolderRecords = Lists.newArrayList();
        unavailableFolderRecords.addAll(prepareRecords);
        prepareRecords.forEach(record -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, record));
        List<Long> unavailableFolderRecordIds = unavailableFolderRecords.stream().map(UserFileDO::getId).collect(Collectors.toList());
        return unavailableFolderRecordIds.contains(targetParentId);
    }

    /**
     * 查找文件夹的所有子文件夹记录
     *
     * @param unavailableFolderRecords
     * @param folderRecordMap
     * @param record
     */
    private void findAllChildFolderRecords(List<UserFileDO> unavailableFolderRecords, Map<Long, List<UserFileDO>> folderRecordMap, UserFileDO record) {
        if (EmptyUtil.isEmpty(record)) {
            return;
        }
        List<UserFileDO> childFolderRecords = folderRecordMap.get(record.getId());
        if (CollectionUtils.isEmpty(childFolderRecords)) {
            return;
        }
        unavailableFolderRecords.addAll(childFolderRecords);
        childFolderRecords.forEach(childRecord -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, childRecord));
    }

    /**
     * 文件转移的条件校验
     * <p>
     * 1、目标文件必须是一个文件夹
     * 2、选中的要转移的文件列表中不能含有目标文件夹以及其子文件夹
     *
     * @param context
     */
    private void checkCopyCondition(CopyFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!isFolder(getById(targetParentId))) {
            throw new FileException(TARGET_FOLDER_TYPE_ERROR);
        }
        List<Long> fileIdList = context.getFileIdList();
        List<UserFileDO> prepareRecords = listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new FileException(FOLDER_INVALID);
        }
    }

    /**
     * 拼装当前文件记录以及所有的子文件记录
     *
     * @param allRecords
     * @param record
     * @param targetParentId
     * @param userId
     */
    private void assembleCopyChildRecord(List<UserFileDO> allRecords, UserFileDO record, Long targetParentId, Long userId) {
        Long newFileId = IdUtil.get();
        Long oldFileId = record.getId();

        record.setParentId(targetParentId);
        record.setId(newFileId);
        record.setUserId(userId);
        record.setCreateUser(userId);
        record.setUpdateUser(userId);
        handleDuplicateFilename(record);

        allRecords.add(record);

        if (isFolder(record)) {
            List<UserFileDO> childRecords = findChildRecords(oldFileId);
            if (CollectionUtils.isEmpty(childRecords)) {
                return;
            }
            childRecords.forEach(childRecord -> assembleCopyChildRecord(allRecords, childRecord, newFileId, userId));
        }
    }

    /**
     * 查找下一级的文件记录
     *
     * @param parentId
     * @return
     */
    private List<UserFileDO> findChildRecords(Long parentId) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        return list(queryWrapper);
    }

    /**
     * 搜索文件列表
     *
     * @param context
     * @return
     */
    private List<FileSearchVO> doSearch(FileSearchContext context) {
        List<UserFileDO> userFileDOList = baseMapper.searchFile(context);
        return fileConvertor.mapToSearchVo(userFileDOList);
    }

    /**
     * 填充文件列表的父文件名称
     *
     * @param result
     */
    private void fillParentFilename(List<FileSearchVO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        List<Long> parentIdList = result.stream().map(FileSearchVO::getParentId).collect(Collectors.toList());
        List<UserFileDO> parentRecords = listByIds(parentIdList);
        Map<Long, String> fileId2filenameMap = parentRecords.stream().collect(Collectors.toMap(UserFileDO::getId, UserFileDO::getFilename));
        result.forEach(vo -> vo.setParentFilename(fileId2filenameMap.get(vo.getParentId())));
    }
}
