package com.disk.ai.facade;

import com.disk.ai.domain.service.AiApplicationService;
import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.AiOperationResponse;
import com.disk.api.ai.response.data.AiDocumentIndexData;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;
import com.disk.api.ai.service.AiFacadeService;
import com.disk.rpc.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class AiFacadeServiceImpl implements AiFacadeService {

    private final AiApplicationService aiApplicationService;

    @Facade
    @Override
    public AiOperationResponse<AiDocumentIndexData> indexFile(AiDocumentIndexRequest request) {
        return AiOperationResponse.success(aiApplicationService.indexFile(request));
    }

    @Facade
    @Override
    public AiOperationResponse<AiSummaryData> summarize(AiDocumentSummaryRequest request) {
        return AiOperationResponse.success(aiApplicationService.summarize(request));
    }

    @Facade
    @Override
    public AiOperationResponse<AiTagData> generateTags(AiDocumentTagRequest request) {
        return AiOperationResponse.success(aiApplicationService.generateTags(request));
    }

    @Facade
    @Override
    public AiOperationResponse<AiFileAnswerData> answerSingleFileQuestion(AiFileQuestionRequest request) {
        return AiOperationResponse.success(aiApplicationService.answerSingleFileQuestion(request));
    }
}
