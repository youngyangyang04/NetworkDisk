package com.disk.files.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;


/**
 * 类描述: 用户文件DO
 *
 * @author weikunkun
 */
@Getter
@Setter
@TableName("user_file")
public class UserFileDO extends BaseEntity {

    /**
     * 文件记录ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 上级文件夹ID,顶级文件夹为0
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 真实文件id
     */
    @TableField(value = "real_file_id")
    private Long realFileId;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 是否是文件夹 （0 否 1 是）
     */
    @TableField(value = "folder_flag")
    private Integer folderFlag;

    /**
     * 文件大小展示字符
     */
    @TableField(value = "file_size_desc")
    private String fileSizeDesc;

    /**
     * 文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     */
    @TableField(value = "file_type")
    private Integer fileType;

    /**
     * 更新人
     */
    @TableField(value = "update_user")
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(value = "create_user")
    private Long createUser;
}
