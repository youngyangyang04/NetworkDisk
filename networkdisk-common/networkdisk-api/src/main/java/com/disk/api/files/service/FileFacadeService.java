package com.disk.api.files.service;

import com.disk.api.files.request.AiFileReadRequest;
import com.disk.api.files.response.FileQueryResponse;
import com.disk.api.files.response.data.AiFileReadData;

public interface FileFacadeService {

    FileQueryResponse<AiFileReadData> getFileReadInfo(AiFileReadRequest request);
}
