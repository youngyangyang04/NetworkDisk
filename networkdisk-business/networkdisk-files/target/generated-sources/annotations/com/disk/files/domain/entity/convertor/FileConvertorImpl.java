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
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.context.UploadFileContext;
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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-17T19:59:13+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class FileConvertorImpl implements FileConvertor {

    @Override
    public CreateFolderContext createFolderParamToCreateFolderContext(CreateFolderParamVO createFolderParam) {
        if ( createFolderParam == null ) {
            return null;
        }

        CreateFolderContext createFolderContext = new CreateFolderContext();

        if ( createFolderParam.getFolderName() != null ) {
            createFolderContext.setFolderName( createFolderParam.getFolderName() );
        }

        createFolderContext.setParentId( com.disk.base.utils.IdUtil.decrypt(createFolderParam.getParentId()) );
        createFolderContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return createFolderContext;
    }

    @Override
    public UpdateFilenameContext updateFilenameParamToUpdateFilenameContext(UpdateFilenameParamVO updateFilenameParam) {
        if ( updateFilenameParam == null ) {
            return null;
        }

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();

        if ( updateFilenameParam.getNewFilename() != null ) {
            updateFilenameContext.setNewFilename( updateFilenameParam.getNewFilename() );
        }

        updateFilenameContext.setFileId( com.disk.base.utils.IdUtil.decrypt(updateFilenameParam.getFileId()) );
        updateFilenameContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return updateFilenameContext;
    }

    @Override
    public DeleteUserFileContext deleteFileParamToDeleteFileContext(DeleteFileParamVO deleteFileParam) {
        if ( deleteFileParam == null ) {
            return null;
        }

        DeleteUserFileContext deleteUserFileContext = new DeleteUserFileContext();

        deleteUserFileContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return deleteUserFileContext;
    }

    @Override
    public List<UserFileVO> mapToVo(List<UserFileDO> request) {
        if ( request == null ) {
            return null;
        }

        List<UserFileVO> list = new ArrayList<UserFileVO>( request.size() );
        for ( UserFileDO userFileDO : request ) {
            list.add( userFileToUserFileVO( userFileDO ) );
        }

        return list;
    }

    @Override
    public List<FileSearchVO> mapToSearchVo(List<UserFileDO> request) {
        if ( request == null ) {
            return null;
        }

        List<FileSearchVO> list = new ArrayList<FileSearchVO>( request.size() );
        for ( UserFileDO userFileDO : request ) {
            list.add( userFileDOToFileSearchVO( userFileDO ) );
        }

        return list;
    }

    @Override
    public SecUploadFileContext secUploadFileParamToSecUploadFileContext(SecUploadFileParamVO secUploadFileParam) {
        if ( secUploadFileParam == null ) {
            return null;
        }

        SecUploadFileContext secUploadFileContext = new SecUploadFileContext();

        if ( secUploadFileParam.getFilename() != null ) {
            secUploadFileContext.setFilename( secUploadFileParam.getFilename() );
        }
        if ( secUploadFileParam.getIdentifier() != null ) {
            secUploadFileContext.setIdentifier( secUploadFileParam.getIdentifier() );
        }

        secUploadFileContext.setParentId( com.disk.base.utils.IdUtil.decrypt(secUploadFileParam.getParentId()) );

        return secUploadFileContext;
    }

    @Override
    public UploadFileContext fileUploadParamToFileUploadContext(FileUploadParamVO fileUploadParam) {
        if ( fileUploadParam == null ) {
            return null;
        }

        UploadFileContext uploadFileContext = new UploadFileContext();

        if ( fileUploadParam.getFilename() != null ) {
            uploadFileContext.setFilename( fileUploadParam.getFilename() );
        }
        if ( fileUploadParam.getIdentifier() != null ) {
            uploadFileContext.setIdentifier( fileUploadParam.getIdentifier() );
        }
        if ( fileUploadParam.getTotalSize() != null ) {
            uploadFileContext.setTotalSize( fileUploadParam.getTotalSize() );
        }
        if ( fileUploadParam.getFile() != null ) {
            uploadFileContext.setFile( fileUploadParam.getFile() );
        }

        uploadFileContext.setParentId( com.disk.base.utils.IdUtil.decrypt(fileUploadParam.getParentId()) );
        uploadFileContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return uploadFileContext;
    }

    @Override
    public SaveFileContext fileUploadContextToFileSaveContext(UploadFileContext context) {
        if ( context == null ) {
            return null;
        }

        SaveFileContext saveFileContext = new SaveFileContext();

        if ( context.getFilename() != null ) {
            saveFileContext.setFilename( context.getFilename() );
        }
        if ( context.getIdentifier() != null ) {
            saveFileContext.setIdentifier( context.getIdentifier() );
        }
        if ( context.getTotalSize() != null ) {
            saveFileContext.setTotalSize( context.getTotalSize() );
        }
        if ( context.getFile() != null ) {
            saveFileContext.setFile( context.getFile() );
        }
        if ( context.getUserId() != null ) {
            saveFileContext.setUserId( context.getUserId() );
        }

        return saveFileContext;
    }

    @Override
    public FileChunkUploadContext fileChunkUploadParamToFileChunkUploadContext(FileChunkUploadParamVO fileChunkUpload) {
        if ( fileChunkUpload == null ) {
            return null;
        }

        FileChunkUploadContext fileChunkUploadContext = new FileChunkUploadContext();

        if ( fileChunkUpload.getFilename() != null ) {
            fileChunkUploadContext.setFilename( fileChunkUpload.getFilename() );
        }
        if ( fileChunkUpload.getIdentifier() != null ) {
            fileChunkUploadContext.setIdentifier( fileChunkUpload.getIdentifier() );
        }
        if ( fileChunkUpload.getTotalChunks() != null ) {
            fileChunkUploadContext.setTotalChunks( fileChunkUpload.getTotalChunks() );
        }
        if ( fileChunkUpload.getChunkNumber() != null ) {
            fileChunkUploadContext.setChunkNumber( fileChunkUpload.getChunkNumber() );
        }
        if ( fileChunkUpload.getCurrentChunkSize() != null ) {
            fileChunkUploadContext.setCurrentChunkSize( fileChunkUpload.getCurrentChunkSize() );
        }
        if ( fileChunkUpload.getTotalSize() != null ) {
            fileChunkUploadContext.setTotalSize( fileChunkUpload.getTotalSize() );
        }
        if ( fileChunkUpload.getFile() != null ) {
            fileChunkUploadContext.setFile( fileChunkUpload.getFile() );
        }

        fileChunkUploadContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return fileChunkUploadContext;
    }

    @Override
    public FileChunkSaveContext fileChunkUploadContextToFileChunkSaveContext(FileChunkUploadContext context) {
        if ( context == null ) {
            return null;
        }

        FileChunkSaveContext fileChunkSaveContext = new FileChunkSaveContext();

        if ( context.getFilename() != null ) {
            fileChunkSaveContext.setFilename( context.getFilename() );
        }
        if ( context.getIdentifier() != null ) {
            fileChunkSaveContext.setIdentifier( context.getIdentifier() );
        }
        if ( context.getTotalChunks() != null ) {
            fileChunkSaveContext.setTotalChunks( context.getTotalChunks() );
        }
        if ( context.getChunkNumber() != null ) {
            fileChunkSaveContext.setChunkNumber( context.getChunkNumber() );
        }
        if ( context.getCurrentChunkSize() != null ) {
            fileChunkSaveContext.setCurrentChunkSize( context.getCurrentChunkSize() );
        }
        if ( context.getTotalSize() != null ) {
            fileChunkSaveContext.setTotalSize( context.getTotalSize() );
        }
        if ( context.getFile() != null ) {
            fileChunkSaveContext.setFile( context.getFile() );
        }
        if ( context.getUserId() != null ) {
            fileChunkSaveContext.setUserId( context.getUserId() );
        }

        return fileChunkSaveContext;
    }

    @Override
    public StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context) {
        if ( context == null ) {
            return null;
        }

        StoreFileChunkContext storeFileChunkContext = new StoreFileChunkContext();

        if ( context.getFilename() != null ) {
            storeFileChunkContext.setFilename( context.getFilename() );
        }
        if ( context.getIdentifier() != null ) {
            storeFileChunkContext.setIdentifier( context.getIdentifier() );
        }
        if ( context.getTotalSize() != null ) {
            storeFileChunkContext.setTotalSize( context.getTotalSize() );
        }
        if ( context.getTotalChunks() != null ) {
            storeFileChunkContext.setTotalChunks( context.getTotalChunks() );
        }
        if ( context.getChunkNumber() != null ) {
            storeFileChunkContext.setChunkNumber( context.getChunkNumber() );
        }
        if ( context.getCurrentChunkSize() != null ) {
            storeFileChunkContext.setCurrentChunkSize( context.getCurrentChunkSize() );
        }
        if ( context.getUserId() != null ) {
            storeFileChunkContext.setUserId( context.getUserId() );
        }

        return storeFileChunkContext;
    }

    @Override
    public QueryUploadedChunksContext queryUploadedChunksParam2QueryUploadedChunksContext(QueryUploadedChunkListParamVO queryUploadedChunksPO) {
        if ( queryUploadedChunksPO == null ) {
            return null;
        }

        QueryUploadedChunksContext queryUploadedChunksContext = new QueryUploadedChunksContext();

        if ( queryUploadedChunksPO.getIdentifier() != null ) {
            queryUploadedChunksContext.setIdentifier( queryUploadedChunksPO.getIdentifier() );
        }

        queryUploadedChunksContext.setUserId( com.disk.base.utils.UserIdUtil.get() );

        return queryUploadedChunksContext;
    }

    @Override
    public FileChunkMergeContext fileChunkMergeParamVOToFileChunkMergeContext(FileChunkMergeParamVO fileChunkMergeParam) {
        if ( fileChunkMergeParam == null ) {
            return null;
        }

        FileChunkMergeContext fileChunkMergeContext = new FileChunkMergeContext();

        if ( fileChunkMergeParam.getFilename() != null ) {
            fileChunkMergeContext.setFilename( fileChunkMergeParam.getFilename() );
        }
        if ( fileChunkMergeParam.getIdentifier() != null ) {
            fileChunkMergeContext.setIdentifier( fileChunkMergeParam.getIdentifier() );
        }
        if ( fileChunkMergeParam.getTotalSize() != null ) {
            fileChunkMergeContext.setTotalSize( fileChunkMergeParam.getTotalSize() );
        }

        fileChunkMergeContext.setUserId( com.disk.base.utils.UserIdUtil.get() );
        fileChunkMergeContext.setParentId( com.disk.base.utils.IdUtil.decrypt(fileChunkMergeParam.getParentId()) );

        return fileChunkMergeContext;
    }

    @Override
    public FolderTreeNodeVO userFile2FolderTreeNodeVO(UserFileDO record) {
        if ( record == null ) {
            return null;
        }

        FolderTreeNodeVO folderTreeNodeVO = new FolderTreeNodeVO();

        if ( record.getFilename() != null ) {
            folderTreeNodeVO.setName( record.getFilename() );
        }
        if ( record.getId() != null ) {
            folderTreeNodeVO.setId( record.getId() );
        }
        if ( record.getParentId() != null ) {
            folderTreeNodeVO.setParentId( record.getParentId() );
        }

        folderTreeNodeVO.setChildren( com.google.common.collect.Lists.newArrayList() );

        return folderTreeNodeVO;
    }

    @Override
    public UserFileVO userFileToUserFileVO(UserFileDO record) {
        if ( record == null ) {
            return null;
        }

        UserFileVO userFileVO = new UserFileVO();

        if ( record.getId() != null ) {
            userFileVO.setId( record.getId() );
        }
        if ( record.getParentId() != null ) {
            userFileVO.setParentId( record.getParentId() );
        }
        if ( record.getFilename() != null ) {
            userFileVO.setFilename( record.getFilename() );
        }
        if ( record.getFileSizeDesc() != null ) {
            userFileVO.setFileSizeDesc( record.getFileSizeDesc() );
        }
        if ( record.getFolderFlag() != null ) {
            userFileVO.setFolderFlag( record.getFolderFlag() );
        }
        if ( record.getFileType() != null ) {
            userFileVO.setFileType( record.getFileType() );
        }

        return userFileVO;
    }

    @Override
    public FileChunkMergeAndSaveContext fileChunkMergeContextToSaveContext(FileChunkMergeContext context) {
        if ( context == null ) {
            return null;
        }

        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = new FileChunkMergeAndSaveContext();

        if ( context.getFilename() != null ) {
            fileChunkMergeAndSaveContext.setFilename( context.getFilename() );
        }
        if ( context.getIdentifier() != null ) {
            fileChunkMergeAndSaveContext.setIdentifier( context.getIdentifier() );
        }
        if ( context.getTotalSize() != null ) {
            fileChunkMergeAndSaveContext.setTotalSize( context.getTotalSize() );
        }
        if ( context.getParentId() != null ) {
            fileChunkMergeAndSaveContext.setParentId( context.getParentId() );
        }
        if ( context.getUserId() != null ) {
            fileChunkMergeAndSaveContext.setUserId( context.getUserId() );
        }
        if ( context.getRecord() != null ) {
            fileChunkMergeAndSaveContext.setRecord( context.getRecord() );
        }

        return fileChunkMergeAndSaveContext;
    }

    @Override
    public UserFileData userFileDOToUserFileData(UserFileDO context) {
        if ( context == null ) {
            return null;
        }

        UserFileData userFileData = new UserFileData();

        if ( context.getId() != null ) {
            userFileData.setId( context.getId() );
        }
        if ( context.getUserId() != null ) {
            userFileData.setUserId( context.getUserId() );
        }
        if ( context.getParentId() != null ) {
            userFileData.setParentId( context.getParentId() );
        }
        if ( context.getRealFileId() != null ) {
            userFileData.setRealFileId( context.getRealFileId() );
        }
        if ( context.getFilename() != null ) {
            userFileData.setFilename( context.getFilename() );
        }
        if ( context.getFolderFlag() != null ) {
            userFileData.setFolderFlag( context.getFolderFlag() );
        }
        if ( context.getFileSizeDesc() != null ) {
            userFileData.setFileSizeDesc( context.getFileSizeDesc() );
        }
        if ( context.getFileType() != null ) {
            userFileData.setFileType( context.getFileType() );
        }
        if ( context.getUpdateUser() != null ) {
            userFileData.setUpdateUser( context.getUpdateUser() );
        }
        if ( context.getCreateUser() != null ) {
            userFileData.setCreateUser( context.getCreateUser() );
        }

        return userFileData;
    }

    protected FileSearchVO userFileDOToFileSearchVO(UserFileDO userFileDO) {
        if ( userFileDO == null ) {
            return null;
        }

        FileSearchVO fileSearchVO = new FileSearchVO();

        if ( userFileDO.getParentId() != null ) {
            fileSearchVO.setParentId( userFileDO.getParentId() );
        }
        if ( userFileDO.getFilename() != null ) {
            fileSearchVO.setFilename( userFileDO.getFilename() );
        }
        if ( userFileDO.getFileSizeDesc() != null ) {
            fileSearchVO.setFileSizeDesc( userFileDO.getFileSizeDesc() );
        }
        if ( userFileDO.getFolderFlag() != null ) {
            fileSearchVO.setFolderFlag( userFileDO.getFolderFlag() );
        }
        if ( userFileDO.getFileType() != null ) {
            fileSearchVO.setFileType( userFileDO.getFileType() );
        }

        return fileSearchVO;
    }
}
