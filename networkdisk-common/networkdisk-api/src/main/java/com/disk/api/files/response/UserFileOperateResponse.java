package com.disk.api.files.response;

import com.disk.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserFileOperateResponse<T> extends BaseResponse {


    @Serial
    private static final long serialVersionUID = 7343094802931376602L;

    private T data;
}
