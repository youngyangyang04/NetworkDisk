package com.disk.ai.infrastructure.file;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.api.files.request.AiFileReadRequest;
import com.disk.api.files.response.FileQueryResponse;
import com.disk.api.files.response.data.AiFileReadData;
import com.disk.api.files.service.FileFacadeService;
import com.disk.base.utils.IdUtil;
import com.disk.file.context.ReadFileContext;
import com.disk.file.core.StorageEngine;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class AiSourceFileLoader {

    private final StorageEngine storageEngine;

    @DubboReference(version = "1.0.0")
    private FileFacadeService fileFacadeService;

    public AiSourceFileLoader(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    public AiSourceFile load(Long userId, String fileId, Long userFileId) {
        Long resolvedUserFileId = userFileId == null ? IdUtil.decrypt(fileId) : userFileId;

        AiFileReadRequest request = new AiFileReadRequest();
        request.setUserId(userId);
        request.setUserFileId(resolvedUserFileId);

        FileQueryResponse<AiFileReadData> response = fileFacadeService.getFileReadInfo(request);
        if (response == null || !Boolean.TRUE.equals(response.getSuccess()) || response.getData() == null) {
            throw new AiException(AiErrorCode.FILE_READ_FAILED.getMessage(), AiErrorCode.FILE_READ_FAILED);
        }

        AiFileReadData data = response.getData();
        byte[] bytes = readBytes(data.getRealPath());

        AiSourceFile sourceFile = new AiSourceFile();
        sourceFile.setUserId(userId);
        sourceFile.setFileId(fileId);
        sourceFile.setUserFileId(resolvedUserFileId);
        sourceFile.setRealFileId(data.getRealFileId());
        sourceFile.setFilename(data.getFilename());
        sourceFile.setFileSuffix(data.getFileSuffix());
        sourceFile.setContentType(data.getFilePreviewContentType());
        sourceFile.setIdentifier(data.getIdentifier());
        sourceFile.setFileSize(data.getFileSize());
        sourceFile.setFileType(data.getFileType());
        sourceFile.setBytes(bytes);
        return sourceFile;
    }

    private byte[] readBytes(String realPath) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ReadFileContext context = new ReadFileContext();
            context.setRealPath(realPath);
            context.setOutputStream(outputStream);
            storageEngine.realFile(context);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AiException("Failed to read source file bytes", e, AiErrorCode.FILE_READ_FAILED);
        }
    }
}
