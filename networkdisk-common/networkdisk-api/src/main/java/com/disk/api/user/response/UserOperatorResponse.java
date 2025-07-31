package com.disk.api.user.response;

import com.disk.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户操作响应
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserOperatorResponse<T> extends BaseResponse {

    private static final long serialVersionUID = -1134821954496052435L;

    private T data;

}
