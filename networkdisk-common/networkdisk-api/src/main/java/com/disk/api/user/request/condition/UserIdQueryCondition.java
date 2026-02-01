package com.disk.api.user.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * @author weikunkun
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserIdQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1218202388932341345L;

    /**
     * 用户ID
     */
    private Long userId;
}
