package com.disk.files.domain.entity.convertor;

import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.context.DeleteUserFileContext;
import com.disk.files.domain.context.SaveFileContext;
import com.disk.files.domain.context.SecUploadFileContext;
import com.disk.files.domain.context.UpdateFilenameContext;
import com.disk.files.domain.context.UploadFileContext;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.request.CreateFolderParamVO;
import com.disk.files.domain.request.DeleteFileParamVO;
import com.disk.files.domain.request.FileUploadParamVO;
import com.disk.files.domain.request.SecUploadFileParamVO;
import com.disk.files.domain.request.UpdateFilenameParamVO;
import com.disk.files.domain.response.UserFileVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-01T20:58:01+0800",
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
            list.add( userFileDOToUserFileVO( userFileDO ) );
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

        uploadFileContext.setParentId( com.disk.base.utils.IdUtil.decrypt(secUploadFileParam.getParentId()) );
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

    protected UserFileVO userFileDOToUserFileVO(UserFileDO userFileDO) {
        if ( userFileDO == null ) {
            return null;
        }

        UserFileVO userFileVO = new UserFileVO();

        if ( userFileDO.getId() != null ) {
            userFileVO.setId( userFileDO.getId() );
        }
        if ( userFileDO.getParentId() != null ) {
            userFileVO.setParentId( userFileDO.getParentId() );
        }
        if ( userFileDO.getFilename() != null ) {
            userFileVO.setFilename( userFileDO.getFilename() );
        }
        if ( userFileDO.getFileSizeDesc() != null ) {
            userFileVO.setFileSizeDesc( userFileDO.getFileSizeDesc() );
        }
        if ( userFileDO.getFolderFlag() != null ) {
            userFileVO.setFolderFlag( userFileDO.getFolderFlag() );
        }
        if ( userFileDO.getFileType() != null ) {
            userFileVO.setFileType( userFileDO.getFileType() );
        }

        return userFileVO;
    }
}
