package com.disk.files.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类描述: 文件DO
 *
 * @author weikunkun
 */
@Setter
@Getter
public class FileDO extends BaseEntity {

    /**
     * 文件id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 文件名称
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 文件物理路径
     */
    @TableField(value = "real_path")
    private String realPath;

    /**
     * 文件实际大小
     */
    @TableField(value = "file_size")
    private String fileSize;

    /**
     * 文件大小展示字符
     */
    @TableField(value = "file_size_desc")
    private String fileSizeDesc;

    /**
     * 文件后缀
     */
    @TableField(value = "file_suffix")
    private String fileSuffix;

    /**
     * 文件预览的响应头Content-Type的值
     */
    @TableField(value = "file_preview_content_type")
    private String filePreviewContentType;

    /**
     * 文件唯一标识
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private Long createUser;
}
