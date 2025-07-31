package com.disk.files.facade;

import com.disk.api.files.request.UserFileOperateReqest;
import com.disk.api.files.request.UserFileQueryRequest;
import com.disk.api.files.request.condition.UserRootFolderQueryCondition;
import com.disk.api.files.response.UserFileOperateResponse;
import com.disk.api.files.response.UserFileQueryResponse;
import com.disk.api.files.response.data.UserFileData;
import com.disk.api.files.service.UserFileFacadeService;
import com.disk.api.user.response.data.UserInfo;
import com.disk.files.domain.context.CreateFolderContext;
import com.disk.files.domain.entity.UserFileDO;
import com.disk.files.domain.entity.convertor.FileConvertor;
import com.disk.files.domain.service.UserFileService;
import com.disk.files.infrastructure.enums.FolderFlagEnum;
import com.disk.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@DubboService(version = "1.0.0")
public class UserFileFacadeImpl implements UserFileFacadeService {

    @Autowired
    private UserFileService userFileService;

    @Autowired
    private FileConvertor fileConvertor;


    @Facade
    @Override
    public UserFileQueryResponse<UserFileData> getUserFileInfo(UserFileQueryRequest request) {
        UserFileDO userFileDO = switch (request.getQueryCondition()) {
            case UserRootFolderQueryCondition queryCondition:
                yield userFileService.getUserRootInfo(queryCondition.getUserId(), FolderFlagEnum.YES);
            default:
                throw new UnsupportedOperationException(request.getQueryCondition() + "'' is not supported");
        };

        UserFileQueryResponse<UserFileData> response = new UserFileQueryResponse();
        response.setSuccess(true);
        UserFileData userFileData = fileConvertor.userFileDOToUserFileData(userFileDO);
        response.setData(userFileData);
        return response;
    }

    @Facade
    @Override
    public UserFileOperateResponse<Long> createUserRootFile(UserFileOperateReqest request) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName(request.getName());
        createFolderContext.setUserId(request.getUserId());
        createFolderContext.setParentId(request.getParentId());
        Long folder = userFileService.createFolder(createFolderContext);
        UserFileOperateResponse<Long> response = new UserFileOperateResponse();
        response.setSuccess(true);
        response.setData(folder);
        return response;
    }
}
