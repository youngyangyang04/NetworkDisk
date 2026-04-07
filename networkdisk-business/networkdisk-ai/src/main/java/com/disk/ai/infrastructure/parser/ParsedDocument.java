package com.disk.ai.infrastructure.parser;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ParsedDocument {

    private String mediaType;

    private String parser;

    private String plainText;

    private List<DocumentBlock> blocks;

    private Map<String, String> metadata;
}
