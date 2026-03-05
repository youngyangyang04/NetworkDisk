package com.disk.recycle.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: 回收站使用的用户文件实体（映射 user_file 表，最小字段集）
 */
@Getter
@Setter
@TableName(value = "user_file")
public class UserFileDO extends BaseEntity {

    @TableId(value = "id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "parent_id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @TableField(value = "real_file_id")
    private Long realFileId;

    @TableField(value = "filename")
    private String filename;

    @TableField(value = "folder_flag")
    private Integer folderFlag;

    @TableField(value = "file_size_desc")
    private String fileSizeDesc;

    @TableField(value = "file_type")
    private Integer fileType;

    @TableField(value = "create_user")
    private Long createUser;

    @TableField(value = "update_user")
    private Long updateUser;
}





