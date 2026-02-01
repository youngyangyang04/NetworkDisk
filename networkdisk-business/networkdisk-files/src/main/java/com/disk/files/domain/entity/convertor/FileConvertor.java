package com.disk.files.domain.entity.convertor;

import com.disk.api.files.response.data.UserFileData;
import com.disk.file.context.StoreFileChunkContext;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.FileChunkMergeAndSaveContext;
import com.disk.files.domain.context.FileChunkMergeContext;
import com.disk.files.domain.context.FileChunkSaveContext;
import com.disk.files.domain.context.FileChunkUploadContext;
import com.disk.files.domain.context.QueryUploadedChunksContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.request.CreateFolderParamVO;
import com.disk.files.domain.request.DeleteFileParamVO;
import com.disk.files.domain.request.FileChunkMergeParamVO;
import com.disk.files.domain.request.FileChunkUploadParamVO;
import com.disk.files.domain.request.FileUploadParamVO;
import com.disk.files.domain.request.QueryUploadedChunkListParamVO;
import com.disk.files.domain.request.SecUploadFileParamVO;
import com.disk.files.domain.request.UpdateFilenameParamVO;
import com.disk.files.domain.response.FileSearchVO;
import com.disk.files.domain.response.FolderTreeNodeVO;
import com.disk.files.domain.response.UserFileVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface FileConvertor {


    @Mapping(target = "parentId", expression = "java(com.disk.base.utils.IdUtil.decrypt(createFolderParam.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    CreateFolderContext createFolderParamToCreateFolderContext(CreateFolderParamVO createFolderParam);

    @Mapping(target = "fileId", expression = "java(com.disk.base.utils.IdUtil.decrypt(updateFilenameParam.getFileId()))")
    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    UpdateFilenameContext updateFilenameParamToUpdateFilenameContext(UpdateFilenameParamVO updateFilenameParam);

    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    DeleteUserFileContext deleteFileParamToDeleteFileContext(DeleteFileParamVO deleteFileParam);

    @Mapping(target = "updateTime", source = "gmtModified")
    List<UserFileVO> mapToVo(List<UserFileDO> request);

    List<FileSearchVO> mapToSearchVo(List<UserFileDO> request);

    @Mapping(target = "parentId", expression = "java(com.disk.base.utils.IdUtil.decrypt(secUploadFileParam.getParentId()))")
    SecUploadFileContext secUploadFileParamToSecUploadFileContext(SecUploadFileParamVO secUploadFileParam);

    @Mapping(target = "parentId", expression = "java(com.disk.base.utils.IdUtil.decrypt(fileUploadParam.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    UploadFileContext fileUploadParamToFileUploadContext(FileUploadParamVO fileUploadParam);

    @Mapping(target = "fileRecord", ignore = true)
    SaveFileContext fileUploadContextToFileSaveContext(UploadFileContext context);

    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    FileChunkUploadContext fileChunkUploadParamToFileChunkUploadContext(FileChunkUploadParamVO fileChunkUpload);


    FileChunkSaveContext fileChunkUploadContextToFileChunkSaveContext(FileChunkUploadContext context);

    @Mapping(target = "realPath", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    QueryUploadedChunksContext queryUploadedChunksParam2QueryUploadedChunksContext(QueryUploadedChunkListParamVO queryUploadedChunksPO);

    @Mapping(target = "userId", expression = "java(com.disk.base.utils.UserIdUtil.get())")
    @Mapping(target = "parentId", expression = "java(com.disk.base.utils.IdUtil.decrypt(fileChunkMergeParam.getParentId()))")
    FileChunkMergeContext fileChunkMergeParamVOToFileChunkMergeContext(FileChunkMergeParamVO fileChunkMergeParam);

    @Mapping(target = "name", source = "record.filename")
    @Mapping(target = "id", source = "record.id")
    @Mapping(target = "children", expression = "java(com.google.common.collect.Lists.newArrayList())")
    FolderTreeNodeVO userFile2FolderTreeNodeVO(UserFileDO record);

    UserFileVO userFileToUserFileVO(UserFileDO record);

    FileChunkMergeAndSaveContext fileChunkMergeContextToSaveContext(FileChunkMergeContext context);

    UserFileData userFileDOToUserFileData(UserFileDO context);
}
