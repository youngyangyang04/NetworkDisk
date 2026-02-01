package com.disk.files.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

/**
 * 类描述: 文件分片DO
 *
 * @author weikunkun
 */
@Setter
@Getter
@TableName(value ="file_chunk")
public class FileChunkDO extends BaseEntity {

    private static final long serialVersionUID = -4558398622548855899L;
    /**
     * 文件唯一标识
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 分片真实的存储路径
     */
    @TableField(value = "real_path")
    private String realPath;

    /**
     * 分片编号
     */
    @TableField(value = "chunk_number")
    private Integer chunkNumber;

    /**
     * 过期时间
     */
    @TableField(value = "expiration_time")
    private Date expirationTime;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private Long createUser;

    /**
     * 更新人
     */
    @TableField(value = "update_user")
    private Long updateUser;
}
