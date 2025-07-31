package com.disk.api.files.response;

import com.disk.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class UserFileQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 4345258618767118583L;

    private T data;
}
