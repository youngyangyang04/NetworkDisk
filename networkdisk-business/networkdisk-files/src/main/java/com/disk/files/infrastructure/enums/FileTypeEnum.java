package com.disk.files.infrastructure.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    NORMAL_FILE(1, "NORMAL_FILE", 1, fileSuffix -> true),
    ARCHIVE_FILE(2, "ARCHIVE_FILE", 2, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".rar", ".zip", ".cab", ".iso", ".jar", ".ace", ".7z", ".tar", ".gz", ".arj", ".lah", ".uue", ".bz2", ".z", ".war");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    EXCEL_FILE(3, "EXCEL", 3, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".xlsx", ".xls");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    WORD_FILE(4, "WORD_FILE", 4, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".docx", ".doc");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    PDF_FILE(5, "PDF_FILE", 5, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".pdf");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    TXT_FILE(6, "TXT_FILE", 6, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".txt");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    IMAGE_FILE(7, "IMAGE_FILE", 7, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".bmp", ".gif", ".png", ".ico", ".eps", ".psd", ".tga", ".tiff", ".jpg", ".jpeg");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    AUDIO_FILE(8, "AUDIO_FILE", 8, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".mp3", ".mkv", ".mpg", ".rm", ".wma");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    VIDEO_FILE(9, "VIDEO_FILE", 9, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".avi", ".3gp", ".mp4", ".flv", ".rmvb", ".mov");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    POWER_POINT_FILE(10, "POWER_POINT_FILE", 10, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".ppt", ".pptx");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    SOURCE_CODE_FILE(11, "SOURCE_CODE_FILE", 11, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".java", ".obj", ".h", ".c", ".html", ".net", ".php", ".css", ".js", ".ftl", ".jsp", ".asp");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    }),
    CSV_FILE(12, "CSV_FILE", 12, fileSuffix -> {
        List<String> matchFileSuffixes = Lists.newArrayList(".csv");
        return StringUtils.isNotBlank(fileSuffix) && matchFileSuffixes.contains(fileSuffix);
    });

    /**
     * 文件类型的code
     */
    private Integer code;

    /**
     * 文件类型描述
     */
    private String desc;

    /**
     * 排序字段
     * 按照降序顺序排序
     */
    private Integer order;

    /**
     * 文件类型匹配器
     */
    private Predicate<String> matcher;

    /**
     * 根据文件名称的后缀获取对应的文件类型映射code
     *
     * @param fileSuffix
     * @return
     */
    public static Integer getFileTypeCode(String fileSuffix) {
        FileTypeEnum fileTypeEnum = Arrays.stream(values())
                .sorted(Comparator.comparingInt(FileTypeEnum::getOrder).reversed())
                .filter(value -> value.getMatcher().test(fileSuffix))
                .findFirst().orElseThrow(() -> new RuntimeException("获取文件类型失败"));
        return fileTypeEnum.getCode();
    }
}
