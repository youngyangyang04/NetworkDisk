package com.disk.api.user.request;

import com.disk.api.user.request.condition.UserEmailQueryCondition;
import com.disk.api.user.request.condition.UserIdQueryCondition;
import com.disk.api.user.request.condition.UserQueryCondition;
import com.disk.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weikunkun
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryRequest extends BaseRequest {

    private UserQueryCondition userQueryCondition;

    public UserQueryRequest(Long userId) {
        UserIdQueryCondition userIdQueryCondition = new UserIdQueryCondition();
        userIdQueryCondition.setUserId(userId);
        this.userQueryCondition = userIdQueryCondition;
    }

    public UserQueryRequest(String email) {
        UserEmailQueryCondition userPhoneQueryCondition = new UserEmailQueryCondition();
        userPhoneQueryCondition.setEmail(email);
        this.userQueryCondition = userPhoneQueryCondition;
    }

}
