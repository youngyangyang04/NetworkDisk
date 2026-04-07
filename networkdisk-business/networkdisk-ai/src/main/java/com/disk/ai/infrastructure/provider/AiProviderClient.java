package com.disk.ai.infrastructure.provider;

import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;

public interface AiProviderClient {

    AiSummaryData summarize(AiDocumentSummaryRequest request, String documentContent);

    AiTagData generateTags(AiDocumentTagRequest request, String documentContent);

    AiFileAnswerData answerSingleFileQuestion(AiFileQuestionRequest request, String documentContent);
}
