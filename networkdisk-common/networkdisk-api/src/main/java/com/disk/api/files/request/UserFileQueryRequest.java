package com.disk.api.files.request;

import com.disk.api.files.request.condition.UserFileQueryCondition;
import com.disk.api.files.request.condition.UserRootFolderQueryCondition;
import com.disk.base.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserFileQueryRequest extends BaseRequest {


    private UserFileQueryCondition queryCondition;

    public UserFileQueryRequest(Long userId) {
        UserRootFolderQueryCondition userRootFolderQueryCondition = new UserRootFolderQueryCondition();
        userRootFolderQueryCondition.setUserId(userId);
        this.queryCondition = userRootFolderQueryCondition;
    }

}
