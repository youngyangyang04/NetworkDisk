package com.disk.ai.exception;

import com.disk.base.exception.ErrorCode;

public enum AiErrorCode implements ErrorCode {

    VECTOR_STORE_DISABLED("AI_VECTOR_STORE_DISABLED", "The pgvector store is disabled or not configured"),
    FILE_READ_FAILED("AI_FILE_READ_FAILED", "Failed to read file content for AI processing"),
    UNSUPPORTED_FILE_TYPE("AI_UNSUPPORTED_FILE_TYPE", "The current file type is not supported for AI parsing"),
    DOCUMENT_PARSE_FAILED("AI_DOCUMENT_PARSE_FAILED", "Failed to parse document content with Apache Tika"),
    DOCUMENT_EMPTY("AI_DOCUMENT_EMPTY", "The parsed document content is empty"),
    DOCUMENT_INDEX_FAILED("AI_DOCUMENT_INDEX_FAILED", "Failed to index the document into pgvector"),
    MODEL_REQUEST_FAILED("AI_MODEL_REQUEST_FAILED", "Failed to call the configured AI model provider");

    private final String code;

    private final String message;

    AiErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
