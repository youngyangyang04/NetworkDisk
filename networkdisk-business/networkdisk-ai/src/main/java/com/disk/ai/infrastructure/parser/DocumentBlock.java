package com.disk.ai.infrastructure.parser;

import lombok.Data;

@Data
public class DocumentBlock {

    private Integer blockIndex;

    private String blockType;

    private String text;

    private Integer startOffset;

    private Integer endOffset;
}
