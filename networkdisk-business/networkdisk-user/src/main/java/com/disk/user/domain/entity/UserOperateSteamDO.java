package com.disk.user.domain.entity;

import com.disk.data.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类描述: 用户操作流水
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserOperateSteamDO extends BaseEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作参数
     */
    private String param;

    /**
     * 扩展字段
     */
    private String extendInfo;

}
