package com.disk.api.files.request;

import com.disk.base.request.BaseRequest;
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
public class UserFileOperateReqest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = -2039864529849644603L;

    private Long parentId;

    private Long userId;

    private String name;

}
