package com.disk.files.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 首页概览数据
 */
@Getter
@Setter
public class HomeOverviewVO {

    /**
     * 文件总数（不含文件夹）
     */
    private Long totalFiles;

    /**
     * 图片文件数量
     */
    private Long images;

    /**
     * 视频文件数量
     */
    private Long videos;

    /**
     * 文档文件数量
     */
    private Long documents;

    /**
     * 最近上传/更新的文件
     */
    private List<UserFileVO> recentFiles;
}

