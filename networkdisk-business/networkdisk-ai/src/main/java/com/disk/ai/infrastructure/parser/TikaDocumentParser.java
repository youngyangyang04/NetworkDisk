package com.disk.ai.infrastructure.parser;

import com.disk.ai.exception.AiErrorCode;
import com.disk.ai.exception.AiException;
import com.disk.ai.infrastructure.config.AiIndexProperties;
import com.disk.ai.infrastructure.file.AiSourceFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TikaDocumentParser {

    private final AiIndexProperties aiIndexProperties;

    public ParsedDocument parse(AiSourceFile sourceFile) {
        validateSupportedFile(sourceFile.getFileSuffix());

        Metadata metadata = new Metadata();
        try (InputStream inputStream = new ByteArrayInputStream(sourceFile.getBytes())) {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            parser.parse(inputStream, handler, metadata, new ParseContext());

            String plainText = normalize(handler.toString());
            if (plainText.length() > aiIndexProperties.getMaxTextChars()) {
                plainText = plainText.substring(0, aiIndexProperties.getMaxTextChars());
            }
            if (StringUtils.isBlank(plainText)) {
                throw new AiException(AiErrorCode.DOCUMENT_EMPTY);
            }

            ParsedDocument parsedDocument = new ParsedDocument();
            parsedDocument.setMediaType(StringUtils.defaultIfBlank(metadata.get(Metadata.CONTENT_TYPE), sourceFile.getContentType()));
            parsedDocument.setParser(parser.getClass().getSimpleName());
            parsedDocument.setPlainText(plainText);
            parsedDocument.setBlocks(splitIntoBlocks(plainText));
            parsedDocument.setMetadata(convertMetadata(metadata));
            return parsedDocument;
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            throw new AiException("Failed to parse document with Apache Tika", e, AiErrorCode.DOCUMENT_PARSE_FAILED);
        }
    }

    private void validateSupportedFile(String fileSuffix) {
        String normalizedSuffix = StringUtils.lowerCase(StringUtils.trimToEmpty(fileSuffix), Locale.ROOT);
        if (!aiIndexProperties.getSupportedFileSuffixes().contains(normalizedSuffix)) {
            throw new AiException(AiErrorCode.UNSUPPORTED_FILE_TYPE);
        }
    }

    private String normalize(String plainText) {
        String normalized = StringUtils.defaultString(plainText)
                .replace("\u0000", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n');
        normalized = normalized.replaceAll("\\n{3,}", "\n\n");
        return normalized.trim();
    }

    private List<DocumentBlock> splitIntoBlocks(String plainText) {
        List<DocumentBlock> blocks = new ArrayList<>();
        String[] parts = plainText.split("(?:\\n\\s*){2,}");
        int cursor = 0;
        int blockIndex = 0;
        for (String part : parts) {
            String text = StringUtils.trimToEmpty(part);
            if (StringUtils.isBlank(text)) {
                continue;
            }
            int start = plainText.indexOf(text, cursor);
            if (start < 0) {
                start = cursor;
            }
            int end = start + text.length();

            DocumentBlock block = new DocumentBlock();
            block.setBlockIndex(blockIndex++);
            block.setBlockType("paragraph");
            block.setText(text);
            block.setStartOffset(start);
            block.setEndOffset(end);
            blocks.add(block);
            cursor = end;
        }
        if (blocks.isEmpty()) {
            DocumentBlock block = new DocumentBlock();
            block.setBlockIndex(0);
            block.setBlockType("document");
            block.setText(plainText);
            block.setStartOffset(0);
            block.setEndOffset(plainText.length());
            blocks.add(block);
        }
        return blocks;
    }

    private Map<String, String> convertMetadata(Metadata metadata) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String name : metadata.names()) {
            String value = metadata.get(name);
            if (StringUtils.isNotBlank(value)) {
                result.put(name, value);
            }
        }
        return result;
    }
}
