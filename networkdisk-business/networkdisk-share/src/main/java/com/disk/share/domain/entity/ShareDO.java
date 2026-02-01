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
@TableName(value ="share")
public class ShareDO extends BaseEntity {

    private static final long serialVersionUID = 151230768074641731L;

    /**
     * 分享名
     */
    @TableField(value = "share_name")
    private String shareName;


    @TableField(value = "share_type")
    private Integer shareType;

    /**
     * 分享时间类型
     */
    @TableField(value = "share_day_type")
    private String shareDayType;

    /**
     * 分享天数
     */
    @TableField(value = "share_day")
    private Integer shareDay;

    /**
     * 结束时间
     */
    @TableField(value = "share_end_time")
    private Date shareEndTime;

    /**
     * 分享链接地址
     */
    @TableField(value = "share_url")
    private String shareUrl;

    /**
     * 分享验证码
     */
    @TableField(value = "share_code")
    private String shareCode;

    /**
     * 分享状态
     */
    @TableField(value = "share_status")
    private Integer shareStatus;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private Long createUser;
}