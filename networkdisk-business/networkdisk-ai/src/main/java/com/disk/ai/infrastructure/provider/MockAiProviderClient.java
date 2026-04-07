package com.disk.ai.infrastructure.provider;

import com.disk.ai.infrastructure.config.AiProviderProperties;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(AiProviderClient.class)
public class MockAiProviderClient implements AiProviderClient {

    private final AiProviderProperties properties;

    @Override
    public AiSummaryData summarize(AiDocumentSummaryRequest request, String documentContent) {
        AiSummaryData response = new AiSummaryData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setSummary(buildSummary(request, documentContent));
        response.setModel(properties.getChatModel());
        response.setMocked(Boolean.TRUE);
        return response;
    }

    @Override
    public AiTagData generateTags(AiDocumentTagRequest request, String documentContent) {
        AiTagData response = new AiTagData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setTags(buildTags(request, documentContent));
        response.setModel(properties.getChatModel());
        response.setMocked(Boolean.TRUE);
        return response;
    }

    @Override
    public AiFileAnswerData answerSingleFileQuestion(AiFileQuestionRequest request, String documentContent) {
        AiFileAnswerData response = new AiFileAnswerData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setQuestion(request.getQuestion());
        response.setAnswer(buildAnswer(request, documentContent));
        response.setReferences(buildReferences(request));
        response.setModel(properties.getChatModel());
        response.setMocked(Boolean.TRUE);
        return response;
    }

    private String buildSummary(AiDocumentSummaryRequest request, String documentContent) {
        String prompt = StringUtils.defaultIfBlank(request.getPrompt(), "default");
        int contentLength = StringUtils.length(documentContent);
        return "Mock summary for " + resolveFilename(request.getFileId(), request.getFilename())
                + ". Replace MockAiProviderClient with a real model provider that consumes indexed document content."
                + " Prompt: " + prompt + "."
                + " ContextLength: " + contentLength + ".";
    }

    private List<String> buildTags(AiDocumentTagRequest request, String documentContent) {
        List<String> candidates = new ArrayList<>();
        candidates.add("mock");
        candidates.add("document");
        candidates.add("summary");
        candidates.add("qa");
        if (StringUtils.isNotBlank(documentContent)) {
            candidates.add("indexed-content");
        }

        String filename = resolveFilename(request.getFileId(), request.getFilename());
        if (StringUtils.isNotBlank(filename)) {
            candidates.add(filename.replace('.', '-'));
        }

        int topK = request.getTopK() == null ? 5 : request.getTopK();
        return candidates.stream().distinct().limit(topK).toList();
    }

    private String buildAnswer(AiFileQuestionRequest request, String documentContent) {
        return "Mock answer for question: " + request.getQuestion()
                + ". The indexing pipeline and retrieval references are wired, but real answer generation still needs a model provider."
                + " ContextLength: " + StringUtils.length(documentContent) + ".";
    }

    private List<String> buildReferences(AiFileQuestionRequest request) {
        if (Boolean.FALSE.equals(request.getIncludeReferences())) {
            return List.of();
        }
        return List.of(
                "fileId:" + request.getFileId(),
                "filename:" + resolveFilename(request.getFileId(), request.getFilename())
        );
    }

    private String resolveFilename(String fileId, String filename) {
        if (StringUtils.isNotBlank(filename)) {
            return filename;
        }
        return "file-" + StringUtils.defaultIfBlank(fileId, "unknown");
    }
}
