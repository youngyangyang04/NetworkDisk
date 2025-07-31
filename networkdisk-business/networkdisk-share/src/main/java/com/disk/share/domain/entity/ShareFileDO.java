package com.disk.share.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Setter
@Getter
@TableName(value ="share_file")
public class ShareFileDO extends BaseEntity {

    private static final long serialVersionUID = 151230768074641758L;

    /**
     * 分享名
     */
    @TableField(value = "share_id")
    private Long shareId;


    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private Long createUser;
}