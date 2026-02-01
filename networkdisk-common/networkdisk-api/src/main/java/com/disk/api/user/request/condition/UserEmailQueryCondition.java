package com.disk.api.user.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weikunkun
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = -6080909404166212851L;

    /**
     * 用户邮箱
     */
    private String email;
}
