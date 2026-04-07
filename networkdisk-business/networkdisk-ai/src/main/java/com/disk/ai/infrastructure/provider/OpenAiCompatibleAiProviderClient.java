package com.disk.ai.infrastructure.provider;

import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "com.disk.ai.provider.type", havingValue = "openai-compatible")
public class OpenAiCompatibleAiProviderClient implements AiProviderClient {

    private final OpenAiCompatibleClient openAiCompatibleClient;

    @Override
    public AiSummaryData summarize(AiDocumentSummaryRequest request, String documentContent) {
        OpenAiCompatibleClient.ChatResult result = openAiCompatibleClient.chatCompletion(
                "You are a document analysis assistant. Summarize the document in concise Chinese. Focus on key purpose, structure, and conclusions.",
                buildSummaryPrompt(request, documentContent)
        );

        AiSummaryData response = new AiSummaryData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setSummary(result.getContent());
        response.setModel(result.getModel());
        response.setMocked(Boolean.FALSE);
        return response;
    }

    @Override
    public AiTagData generateTags(AiDocumentTagRequest request, String documentContent) {
        OpenAiCompatibleClient.ChatResult result = openAiCompatibleClient.chatCompletion(
                "You are a document tagging assistant. Return only concise Chinese tags separated by commas.",
                buildTagPrompt(request, documentContent)
        );

        AiTagData response = new AiTagData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setTags(parseTags(result.getContent(), request.getTopK()));
        response.setModel(result.getModel());
        response.setMocked(Boolean.FALSE);
        return response;
    }

    @Override
    public AiFileAnswerData answerSingleFileQuestion(AiFileQuestionRequest request, String documentContent) {
        OpenAiCompatibleClient.ChatResult result = openAiCompatibleClient.chatCompletion(
                "You are a single-file question answering assistant. Answer in Chinese and use only the provided document context. If the context is insufficient, say so explicitly.",
                buildQuestionPrompt(request, documentContent)
        );

        AiFileAnswerData response = new AiFileAnswerData();
        response.setUserId(request.getUserId());
        response.setFileId(request.getFileId());
        response.setFilename(resolveFilename(request.getFileId(), request.getFilename()));
        response.setQuestion(request.getQuestion());
        response.setAnswer(result.getContent());
        response.setModel(result.getModel());
        response.setMocked(Boolean.FALSE);
        return response;
    }

    private String buildSummaryPrompt(AiDocumentSummaryRequest request, String documentContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Filename: ").append(resolveFilename(request.getFileId(), request.getFilename())).append("\n");
        prompt.append("If an extra user prompt is present, follow it:\n");
        prompt.append(StringUtils.defaultIfBlank(request.getPrompt(), "No extra prompt")).append("\n\n");
        prompt.append("Document content:\n");
        prompt.append(StringUtils.defaultIfBlank(documentContent, "No content"));
        return prompt.toString();
    }

    private String buildTagPrompt(AiDocumentTagRequest request, String documentContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Filename: ").append(resolveFilename(request.getFileId(), request.getFilename())).append("\n");
        prompt.append("Generate at most ").append(request.getTopK() == null ? 5 : request.getTopK()).append(" tags.\n");
        prompt.append("Return only tags separated by commas, no explanation.\n\n");
        prompt.append("Document content:\n");
        prompt.append(StringUtils.defaultIfBlank(documentContent, "No content"));
        return prompt.toString();
    }

    private String buildQuestionPrompt(AiFileQuestionRequest request, String documentContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Filename: ").append(resolveFilename(request.getFileId(), request.getFilename())).append("\n");
        prompt.append("Question: ").append(StringUtils.defaultString(request.getQuestion())).append("\n\n");
        prompt.append("Document context:\n");
        prompt.append(StringUtils.defaultIfBlank(documentContent, "No content"));
        return prompt.toString();
    }

    private List<String> parseTags(String content, Integer topK) {
        int limit = topK == null ? 5 : topK;
        String normalized = StringUtils.defaultString(content)
                .replace(',', '\n')
                .replace(';', '\n');

        return Arrays.stream(normalized.split("\\n"))
                .map(String::trim)
                .map(value -> value.replaceAll("^[\\-*\\d.\\s]+", ""))
                .filter(StringUtils::isNotBlank)
                .map(value -> StringUtils.substring(value, 0, Math.min(value.length(), 30)))
                .distinct()
                .limit(limit)
                .toList();
    }

    private String resolveFilename(String fileId, String filename) {
        if (StringUtils.isNotBlank(filename)) {
            return filename;
        }
        return "file-" + StringUtils.defaultIfBlank(fileId, "unknown");
    }
}
