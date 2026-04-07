package com.disk.files.facade;

import com.disk.api.files.request.AiFileReadRequest;
import com.disk.api.files.response.FileQueryResponse;
import com.disk.api.files.response.data.AiFileReadData;
import com.disk.api.files.service.FileFacadeService;
import com.disk.base.utils.EmptyUtil;
import com.disk.files.domain.entity.FileDO;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.service.FileService;
import com.disk.files.domain.service.UserFileService;
import com.disk.files.exception.FileException;
import com.disk.files.infrastructure.enums.FolderFlagEnum;
import com.disk.rpc.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import static com.disk.files.exception.FilesErrorCode.FILE_NOT_EXIT;
import static com.disk.files.exception.FilesErrorCode.FILE_NO_AUTH;

@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class FileFacadeServiceImpl implements FileFacadeService {

    private final UserFileService userFileService;

    private final FileService fileService;

    @Facade
    @Override
    public FileQueryResponse<AiFileReadData> getFileReadInfo(AiFileReadRequest request) {
        UserFileDO userFileDO = userFileService.getById(request.getUserFileId());
        if (EmptyUtil.isEmpty(userFileDO) || !request.getUserId().equals(userFileDO.getUserId())) {
            throw new FileException(FILE_NO_AUTH);
        }
        if (FolderFlagEnum.YES.getCode().equals(userFileDO.getFolderFlag())) {
            throw new FileException(FILE_NOT_EXIT);
        }

        FileDO fileDO = fileService.getById(userFileDO.getRealFileId());
        if (EmptyUtil.isEmpty(fileDO)) {
            throw new FileException(FILE_NOT_EXIT);
        }

        AiFileReadData data = new AiFileReadData();
        data.setUserId(request.getUserId());
        data.setUserFileId(userFileDO.getId());
        data.setRealFileId(fileDO.getId());
        data.setFilename(userFileDO.getFilename());
        data.setRealPath(fileDO.getRealPath());
        data.setFileSuffix(fileDO.getFileSuffix());
        data.setFilePreviewContentType(fileDO.getFilePreviewContentType());
        data.setIdentifier(fileDO.getIdentifier());
        data.setFileSize(fileDO.getFileSize());
        data.setFileType(userFileDO.getFileType());

        FileQueryResponse<AiFileReadData> response = new FileQueryResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setData(data);
        return response;
    }
}
