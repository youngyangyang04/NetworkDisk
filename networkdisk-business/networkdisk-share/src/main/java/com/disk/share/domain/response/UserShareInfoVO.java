package com.disk.share.domain.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class UserShareInfoVO {

    /**
     * shareId
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    /**
     * 分享名
     */
    private String shareName;


    private Integer shareType;

    /**
     * 分享时间类型
     */
    private String shareDayType;

    /**
     * 分享天数
     */
    private Integer shareDay;

    /**
     * 结束时间
     */
    private Date shareEndTime;

    /**
     * 分享链接地址
     */
    private String shareUrl;

    /**
     * 分享验证码
     */
    private String shareCode;

    /**
     * 分享状态
     */
    private Integer shareStatus;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;
}
