package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileChunkMergeParamVO {

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    /**
     * 文件唯一标识符
     */
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    /**
     * 文件总大小
     */
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    /**
     * 文件的父id
     */
    @NotBlank(message = "文件的父文件夹ID不能为空")
    private String parentId;

}
