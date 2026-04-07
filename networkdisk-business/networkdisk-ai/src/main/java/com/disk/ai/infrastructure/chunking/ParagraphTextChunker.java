package com.disk.ai.infrastructure.chunking;

import com.disk.ai.infrastructure.config.AiIndexProperties;
import com.disk.ai.infrastructure.parser.DocumentBlock;
import com.disk.ai.infrastructure.parser.ParsedDocument;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ParagraphTextChunker {

    private final AiIndexProperties aiIndexProperties;

    public List<TextChunk> chunk(ParsedDocument parsedDocument) {
        List<TextChunk> result = new ArrayList<>();
        int globalChunkIndex = 0;
        for (DocumentBlock block : parsedDocument.getBlocks()) {
            String blockText = StringUtils.trimToEmpty(block.getText());
            if (StringUtils.isBlank(blockText)) {
                continue;
            }
            int start = 0;
            while (start < blockText.length()) {
                int rawEnd = Math.min(start + aiIndexProperties.getChunkSize(), blockText.length());
                int end = adjustEnd(blockText, start, rawEnd);
                String chunkText = StringUtils.trimToEmpty(blockText.substring(start, end));
                if (StringUtils.isNotBlank(chunkText)) {
                    TextChunk chunk = new TextChunk();
                    chunk.setChunkIndex(globalChunkIndex++);
                    chunk.setBlockIndex(block.getBlockIndex());
                    chunk.setText(chunkText);
                    chunk.setStartOffset(block.getStartOffset() + start);
                    chunk.setEndOffset(block.getStartOffset() + end);
                    chunk.setTokenEstimate(Math.max(1, (int) Math.ceil(chunkText.length() / 4.0d)));
                    result.add(chunk);
                }
                if (end >= blockText.length()) {
                    break;
                }
                start = Math.max(end - aiIndexProperties.getChunkOverlap(), start + 1);
            }
        }
        if (result.isEmpty() && StringUtils.isNotBlank(parsedDocument.getPlainText())) {
            TextChunk fallback = new TextChunk();
            fallback.setChunkIndex(0);
            fallback.setBlockIndex(0);
            fallback.setText(parsedDocument.getPlainText());
            fallback.setStartOffset(0);
            fallback.setEndOffset(parsedDocument.getPlainText().length());
            fallback.setTokenEstimate(Math.max(1, (int) Math.ceil(parsedDocument.getPlainText().length() / 4.0d)));
            result.add(fallback);
        }
        return result;
    }

    private int adjustEnd(String text, int start, int rawEnd) {
        if (rawEnd >= text.length()) {
            return text.length();
        }
        int candidate = -1;
        for (int i = rawEnd; i > start + (aiIndexProperties.getChunkSize() / 2); i--) {
            if (Character.isWhitespace(text.charAt(i - 1))) {
                candidate = i - 1;
                break;
            }
        }
        return candidate > start ? candidate : rawEnd;
    }
}
