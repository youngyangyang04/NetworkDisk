package com.disk.api.files.service;

import com.disk.api.files.request.UserFileOperateReqest;
import com.disk.api.files.request.UserFileQueryRequest;
import com.disk.api.files.response.UserFileOperateResponse;
import com.disk.api.files.response.UserFileQueryResponse;
import com.disk.api.files.response.data.UserFileData;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface UserFileFacadeService {

    UserFileQueryResponse<UserFileData> getUserFileInfo(UserFileQueryRequest request);

    UserFileOperateResponse<Long> createUserRootFile(UserFileOperateReqest request);
}
