package com.disk.ai.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "com.disk.ai.index")
public class AiIndexProperties {

    private Integer chunkSize = 1200;

    private Integer chunkOverlap = 200;

    private Integer retrievalTopK = 6;

    private Integer maxTextChars = 200000;

    private List<String> supportedFileSuffixes = List.of(
            ".pdf",
            ".doc",
            ".docx",
            ".txt",
            ".md",
            ".markdown",
            ".csv",
            ".xls",
            ".xlsx",
            ".ppt",
            ".pptx",
            ".html",
            ".htm",
            ".xml",
            ".json",
            ".sql",
            ".java",
            ".js",
            ".css"
    );
}
