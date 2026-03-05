package com.disk.share.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Physical file record (from table file), used by share module.
 */
@Getter
@Setter
@TableName("file")
public class FileDO extends BaseEntity {

    @TableField("real_path")
    private String realPath;

    @TableField("file_size")
    private String fileSize;

    @TableField("file_preview_content_type")
    private String filePreviewContentType;
}
