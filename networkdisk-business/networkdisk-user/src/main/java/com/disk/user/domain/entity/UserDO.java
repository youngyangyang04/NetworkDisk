package com.disk.user.domain.entity;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.disk.api.user.constant.UserRole;
import com.disk.api.user.constant.UserStateEnum;
import com.disk.base.utils.PasswordUtil;
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
     * 使用容量
     */
    private Long useSpace;

    /**
     * 总容量
     */
    private Long totalSpace;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String passwordHash;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;


    public UserDO register(String email, String nickName, String password) {
        this.setEmail(email);
        this.setNickName(nickName);
        this.setPasswordHash(PasswordUtil.encryptPassword(password));
        this.setDeleted(0);
        return this;
    }
}
