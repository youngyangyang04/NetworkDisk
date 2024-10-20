package com.disk.auth.response;

import cn.dev33.satoken.stp.StpUtil;
import com.disk.api.user.response.data.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginResVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标识，如用户ID
     */
    private String userId;
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private Long tokenExpiration;

    public LoginResVO(UserInfo userInfoVO) {
        this.userId = userInfoVO.getUserId().toString();
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }

}
