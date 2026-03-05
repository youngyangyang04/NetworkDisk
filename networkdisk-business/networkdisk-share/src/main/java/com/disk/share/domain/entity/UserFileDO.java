package com.disk.share.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * User file record (from table user_file), used by share module.
 */
@Getter
@Setter
@TableName("user_file")
public class UserFileDO extends BaseEntity {

    @TableField("user_id")
    private Long userId;

    @TableField("parent_id")
    private Long parentId;

    @TableField("real_file_id")
    private Long realFileId;

    @TableField("filename")
    private String filename;

    @TableField("folder_flag")
    private Integer folderFlag;

    @TableField("file_size_desc")
    private String fileSizeDesc;

    @TableField("file_type")
    private Integer fileType;
}
