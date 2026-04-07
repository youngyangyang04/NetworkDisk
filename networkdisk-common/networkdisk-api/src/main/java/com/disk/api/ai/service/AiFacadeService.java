package com.disk.api.ai.service;

import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.AiOperationResponse;
import com.disk.api.ai.response.data.AiDocumentIndexData;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;

public interface AiFacadeService {

    AiOperationResponse<AiDocumentIndexData> indexFile(AiDocumentIndexRequest request);

    AiOperationResponse<AiSummaryData> summarize(AiDocumentSummaryRequest request);

    AiOperationResponse<AiTagData> generateTags(AiDocumentTagRequest request);

    AiOperationResponse<AiFileAnswerData> answerSingleFileQuestion(AiFileQuestionRequest request);
}
