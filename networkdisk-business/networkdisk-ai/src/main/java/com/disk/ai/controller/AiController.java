package com.disk.ai.controller;

import com.disk.ai.domain.request.DocumentIndexParamVO;
import com.disk.ai.domain.request.DocumentSummaryParamVO;
import com.disk.ai.domain.request.GenerateDocumentTagsParamVO;
import com.disk.ai.domain.request.SingleFileQuestionParamVO;
import com.disk.ai.domain.response.AiCapabilityVO;
import com.disk.ai.domain.response.DocumentIndexVO;
import com.disk.ai.domain.response.DocumentSummaryVO;
import com.disk.ai.domain.response.DocumentTagsVO;
import com.disk.ai.domain.response.SingleFileAnswerVO;
import com.disk.ai.domain.service.AiApplicationService;
import com.disk.api.ai.request.AiDocumentIndexRequest;
import com.disk.api.ai.request.AiDocumentSummaryRequest;
import com.disk.api.ai.request.AiDocumentTagRequest;
import com.disk.api.ai.request.AiFileQuestionRequest;
import com.disk.api.ai.response.data.AiDocumentIndexData;
import com.disk.api.ai.response.data.AiFileAnswerData;
import com.disk.api.ai.response.data.AiSummaryData;
import com.disk.api.ai.response.data.AiTagData;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.web.annotation.LoginIgnore;
import com.disk.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiApplicationService aiApplicationService;

    @LoginIgnore
    @GetMapping("/capabilities")
    public Result<AiCapabilityVO> capabilities() {
        return Result.success(aiApplicationService.getCapabilities());
    }

    @PostMapping("/files/index")
    public Result<DocumentIndexVO> indexFile(@Valid @RequestBody DocumentIndexParamVO request) {
        AiDocumentIndexRequest indexRequest = new AiDocumentIndexRequest();
        indexRequest.setUserId(UserIdUtil.get());
        indexRequest.setFileId(request.getFileId());
        indexRequest.setUserFileId(IdUtil.decrypt(request.getFileId()));
        indexRequest.setFilename(request.getFilename());
        indexRequest.setForceReindex(request.getForceReindex());

        AiDocumentIndexData data = aiApplicationService.indexFile(indexRequest);
        DocumentIndexVO response = new DocumentIndexVO();
        response.setFileId(data.getFileId());
        response.setFilename(data.getFilename());
        response.setMediaType(data.getMediaType());
        response.setParser(data.getParser());
        response.setBlockCount(data.getBlockCount());
        response.setChunkCount(data.getChunkCount());
        response.setVectorDimension(data.getVectorDimension());
        response.setContentLength(data.getContentLength());
        response.setIndexed(data.getIndexed());
        response.setReindexed(data.getReindexed());
        response.setVectorStoreEnabled(data.getVectorStoreEnabled());
        return Result.success(response);
    }

    @PostMapping("/files/summary")
    public Result<DocumentSummaryVO> summarize(@Valid @RequestBody DocumentSummaryParamVO request) {
        AiDocumentSummaryRequest summaryRequest = new AiDocumentSummaryRequest();
        summaryRequest.setUserId(UserIdUtil.get());
        summaryRequest.setFileId(request.getFileId());
        summaryRequest.setUserFileId(IdUtil.decrypt(request.getFileId()));
        summaryRequest.setFilename(request.getFilename());
        summaryRequest.setPrompt(request.getPrompt());

        AiSummaryData data = aiApplicationService.summarize(summaryRequest);
        DocumentSummaryVO response = new DocumentSummaryVO();
        response.setFileId(data.getFileId());
        response.setFilename(data.getFilename());
        response.setSummary(data.getSummary());
        response.setModel(data.getModel());
        response.setMocked(data.getMocked());
        return Result.success(response);
    }

    @PostMapping("/files/tags")
    public Result<DocumentTagsVO> generateTags(@Valid @RequestBody GenerateDocumentTagsParamVO request) {
        AiDocumentTagRequest tagRequest = new AiDocumentTagRequest();
        tagRequest.setUserId(UserIdUtil.get());
        tagRequest.setFileId(request.getFileId());
        tagRequest.setUserFileId(IdUtil.decrypt(request.getFileId()));
        tagRequest.setFilename(request.getFilename());
        tagRequest.setTopK(request.getTopK());

        AiTagData data = aiApplicationService.generateTags(tagRequest);
        DocumentTagsVO response = new DocumentTagsVO();
        response.setFileId(data.getFileId());
        response.setFilename(data.getFilename());
        response.setTags(data.getTags());
        response.setModel(data.getModel());
        response.setMocked(data.getMocked());
        return Result.success(response);
    }

    @PostMapping("/files/question")
    public Result<SingleFileAnswerVO> answerSingleFileQuestion(@Valid @RequestBody SingleFileQuestionParamVO request) {
        AiFileQuestionRequest questionRequest = new AiFileQuestionRequest();
        questionRequest.setUserId(UserIdUtil.get());
        questionRequest.setFileId(request.getFileId());
        questionRequest.setUserFileId(IdUtil.decrypt(request.getFileId()));
        questionRequest.setFilename(request.getFilename());
        questionRequest.setQuestion(request.getQuestion());
        questionRequest.setIncludeReferences(request.getIncludeReferences());

        AiFileAnswerData data = aiApplicationService.answerSingleFileQuestion(questionRequest);
        SingleFileAnswerVO response = new SingleFileAnswerVO();
        response.setFileId(data.getFileId());
        response.setFilename(data.getFilename());
        response.setQuestion(data.getQuestion());
        response.setAnswer(data.getAnswer());
        response.setReferences(data.getReferences());
        response.setModel(data.getModel());
        response.setMocked(data.getMocked());
        return Result.success(response);
    }
}
