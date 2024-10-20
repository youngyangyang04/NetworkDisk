package com.disk.user.domain.entity;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.api.user.constant.UserRole;
import com.disk.api.user.constant.UserStateEnum;
import com.disk.data.domain.BaseEntity;
import com.disk.user.infrastructure.mapper.AesEncryptTypeHandler;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类描述: 用户实体
 *
 * @author weikunkun
 */
@Getter
@Setter
@TableName("user")
public class UserDO extends BaseEntity {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String passwordHash;

    /**
     * 状态
     */
    private UserStateEnum state;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人用户ID
     */
    private String inviterId;

    /**
     * 手机号
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;

    /**
     * 实名认证
     */
    private Boolean certification;

    /**
     * 真实姓名
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String realName;

    /**
     * 身份证hash
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String idCardNo;

    /**
     * 用户角色
     */
    private UserRole userRole;


    public UserDO register(String telephone, String nickName, String password, String inviteCode, String inviterId) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.INIT);
        this.setUserRole(UserRole.CUSTOMER);
        this.setInviteCode(inviteCode);
        this.setInviterId(inviterId);
        this.setDeleted(0);
        return this;
    }

    public UserDO register(String telephone, String nickName, String password) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.INIT);
        this.setUserRole(UserRole.CUSTOMER);
        this.setDeleted(0);
        return this;
    }

    public UserDO auth(String realName, String idCard) {
        this.setRealName(realName);
        this.setIdCardNo(idCard);
        this.setCertification(true);
        this.setState(UserStateEnum.AUTH);
        return this;
    }

    public UserDO active() {
        this.setState(UserStateEnum.ACTIVE);
        return this;
    }

    public boolean canModifyInfo() {
        return state == UserStateEnum.INIT || state == UserStateEnum.AUTH || state == UserStateEnum.ACTIVE;
    }
}
