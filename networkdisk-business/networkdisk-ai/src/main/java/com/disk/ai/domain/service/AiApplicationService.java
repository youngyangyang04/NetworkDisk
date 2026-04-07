package com.disk.ai.domain.service;

import com.disk.ai.domain.response.AiCapabilityVO;
import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiDocumentIndexData;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;

public interface AiApplicationService {

    AiCapabilityVO getCapabilities();

    AiDocumentIndexData indexFile(AiDocumentIndexRequest request);

    AiSummaryData summarize(AiDocumentSummaryRequest request);

    AiTagData generateTags(AiDocumentTagRequest request);

    AiFileAnswerData answerSingleFileQuestion(AiFileQuestionRequest request);
}
