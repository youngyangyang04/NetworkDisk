package com.disk.files.controller;

import com.disk.base.constant.BaseConstant;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.entity.convertor.FileConvertor;
import com.disk.files.domain.request.CreateFolderParamVO;
import com.disk.files.domain.request.DeleteFileParamVO;
import com.disk.files.domain.request.FileUploadParamVO;
import com.disk.files.domain.request.SecUploadFileParamVO;
import com.disk.files.domain.request.UpdateFilenameParamVO;
import com.disk.files.domain.response.UserFileVO;
import com.disk.files.domain.service.UserFileService;
import com.disk.files.infrastructure.constant.FileConstant;
import com.disk.base.enums.DeleteEnum;
import com.disk.web.vo.Result;
import com.google.common.base.Splitter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类描述: 文件相关Controller
 *
 * @author weikunkun
 */
@Validated
@RestController()
public class UserFileController {

    @Autowired
    private UserFileService userFileService;
    
    @Autowired
    private FileConvertor fileConvertor;

    /**
     * 查询文件列表
     */
    @GetMapping("/files")
    public Result<List<UserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
                                         @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstant.ALL_FILE_TYPE) String fileTypes) {
        Long realParentId = -1L;
        // 不查询所有类型，父Id为有效值，解密
        if (!Objects.equals(FileConstant.ALL_FILE_TYPE, parentId)) {
            realParentId = IdUtil.decrypt(parentId);
        }
        List<Integer> fileTypeArray = null;
        if (!Objects.equals(FileConstant.ALL_FILE_TYPE, fileTypes)) {
            fileTypeArray = Splitter.on(BaseConstant.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }

        QueryFileContext request = new QueryFileContext();
        request.setParentId(realParentId);
        request.setFileTypeArray(fileTypeArray);
        request.setUserId(UserIdUtil.get());
        request.setDeleted(DeleteEnum.NO.getCode());

        List<UserFileDO> result = userFileService.getUserFileList(request);
        List<UserFileVO> userFileVOList = fileConvertor.mapToVo(result);
        return Result.success(userFileVOList);
    }

    /**
     * 文件夹创建
     */
    @PostMapping("/file/folder")
    public Result<String> createFolder(@Validated @RequestBody CreateFolderParamVO createFolder) {
        CreateFolderContext context = fileConvertor.createFolderParamToCreateFolderContext(createFolder);
        Long fileId = userFileService.createFolder(context);
        return Result.success(IdUtil.encrypt(fileId));
    }

    /**
     * 文件重命名
     */
    @PutMapping("/file")
    public Result updateFilename(@Validated @RequestBody UpdateFilenameParamVO updateFilenameParam) {
        UpdateFilenameContext context = fileConvertor.updateFilenameParamToUpdateFilenameContext(updateFilenameParam);
        userFileService.updateFilename(context);
        return Result.success();
    }


    /**
     * 批量删除文件
     */
    @DeleteMapping("/file")
    public Result deleteFile(@Validated @RequestBody DeleteFileParamVO deleteFileParam) {
        DeleteUserFileContext context = fileConvertor.deleteFileParamToDeleteFileContext(deleteFileParam);
        List<Long> fileIdList = deleteFileParam.getFileIds().stream().map(IdUtil::decrypt).distinct().collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        userFileService.deleteFile(context);
        return Result.success();
    }

    /**
     * 文件秒传
     */
    @PostMapping("file/sec-upload")
    public Result secUpload(@Validated @RequestBody SecUploadFileParamVO secUploadFileParam) {
        SecUploadFileContext context = fileConvertor.secUploadFileParamToSecUploadFileContext(secUploadFileParam);
        boolean result = userFileService.secUpload(context);
        if (!result) {
            return Result.error("", "文件不存在，请重试");
        }
        return Result.success("");
    }
//
    /**
     * 单文件上传
     */
    @PostMapping("file/upload")
    public Result upload(@Validated FileUploadParamVO fileUploadParam) {
        UploadFileContext context = fileConvertor.fileUploadParamToFileUploadContext(fileUploadParam);
        userFileService.upload(context);
        return Result.success("");
    }
//
//    /**
//     * 文件分片上传
//     */
//    @PostMapping("file/chunk-upload")
//    public Result<FileChunkUploadVO> chunkUpload(@Validated FileChunkUploadPO fileChunkUploadPO) {
//        FileChunkUploadContext context = fileConvertor.fileChunkUploadPO2FileChunkUploadContext(fileChunkUploadPO);
//        FileChunkUploadVO vo = userFileService.chunkUpload(context);
//        return Result.success(vo);
//    }
//
//    /**
//     * 查询已经上传的文件分片列表
//     */
//    @GetMapping("file/chunk-upload")
//    public Result<UploadedChunksVO> getUploadedChunks(@Validated QueryUploadedChunksPO queryUploadedChunksPO) {
//        QueryUploadedChunksContext context = fileConvertor.queryUploadedChunksPO2QueryUploadedChunksContext(queryUploadedChunksPO);
//        UploadedChunksVO vo = userFileService.getUploadedChunks(context);
//        return Result.success(vo);
//    }
//
//    /**
//     * 文件分片合并
//     */
//    @PostMapping("file/merge")
//    public Result mergeFile(@Validated @RequestBody FileChunkMergePO fileChunkMergePO) {
//        FileChunkMergeContext context = fileConvertor.fileChunkMergePO2FileChunkMergeContext(fileChunkMergePO);
//        userFileService.mergeFile(context);
//        return Result.success("");
//    }
//
//    /**
//     * 文件下载
//     */
//    @GetMapping("file/download")
//    public void download(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId", required = false) String fileId,
//                         HttpServletResponse response) {
//        FileDownloadContext context = new FileDownloadContext();
//        context.setFileId(IdUtil.decrypt(fileId));
//        context.setResponse(response);
//        context.setUserId(UserIdUtil.get());
//        userFileService.download(context);
//    }
//
//    /**
//     * 文件预览
//     */
//    @GetMapping("file/preview")
//    public void preview(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId", required = false) String fileId,
//                        HttpServletResponse response) {
//        FilePreviewContext context = new FilePreviewContext();
//        context.setFileId(IdUtil.decrypt(fileId));
//        context.setResponse(response);
//        context.setUserId(UserIdUtil.get());
//        userFileService.preview(context);
//    }
//
//    /**
//     * 查询文件夹树
//     */
//    @GetMapping("file/folder/tree")
//    public Result<List<FolderTreeNodeVO>> getFolderTree() {
//        QueryFolderTreeContext context = new QueryFolderTreeContext();
//        context.setUserId(UserIdUtil.get());
//        List<FolderTreeNodeVO> result = userFileService.getFolderTree(context);
//        return Result.success(result);
//    }
//
//    /**
//     * 文件转移
//     */
//    @PostMapping("file/transfer")
//    public Result transfer(@Validated @RequestBody TransferFilePO transferFilePO) {
//        String fileIds = transferFilePO.getFileIds();
//        String targetParentId = transferFilePO.getTargetParentId();
//        List<Long> fileIdList = Splitter.on(RPanConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
//        TransferFileContext context = new TransferFileContext();
//        context.setFileIdList(fileIdList);
//        context.setTargetParentId(IdUtil.decrypt(targetParentId));
//        context.setUserId(UserIdUtil.get());
//        userFileService.transfer(context);
//        return Result.success("");
//    }
//
//    /**
//     * 文件复制
//     */
//    @PostMapping("file/copy")
//    public Result copy(@Validated @RequestBody CopyFilePO copyFilePO) {
//        String fileIds = copyFilePO.getFileIds();
//        String targetParentId = copyFilePO.getTargetParentId();
//        List<Long> fileIdList = Splitter.on(RPanConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
//        CopyFileContext context = new CopyFileContext();
//        context.setFileIdList(fileIdList);
//        context.setTargetParentId(IdUtil.decrypt(targetParentId));
//        context.setUserId(UserIdUtil.get());
//        userFileService.copy(context);
//        return Result.success("");
//    }
//
//    /**
//     * 文件搜索
//     */
//    @GetMapping("file/search")
//    public Result<List<FileSearchResultVO>> search(@Validated FileSearchPO fileSearchPO) {
//        FileSearchContext context = new FileSearchContext();
//        context.setKeyword(fileSearchPO.getKeyword());
//        context.setUserId(UserIdUtil.get());
//        String fileTypes = fileSearchPO.getFileTypes();
//        if (StringUtils.isNotBlank(fileTypes) && !Objects.equals(FileConstant.ALL_FILE_TYPE, fileTypes)) {
//            List<Integer> fileTypeArray = Splitter.on(RPanConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
//            context.setFileTypeArray(fileTypeArray);
//        }
//        List<FileSearchResultVO> result = userFileService.search(context);
//        return Result.success(result);
//    }
//
//    /**
//     * 查询面包屑列表
//     */
//    @GetMapping("file/breadcrumbs")
//    public Result<List<BreadcrumbVO>> getBreadcrumbs(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId", required = false) String fileId) {
//        QueryBreadcrumbsContext context = new QueryBreadcrumbsContext();
//        context.setFileId(IdUtil.decrypt(fileId));
//        context.setUserId(UserIdUtil.get());
//        List<BreadcrumbVO> result = userFileService.getBreadcrumbs(context);
//        return Result.success(result);
//    }

}
