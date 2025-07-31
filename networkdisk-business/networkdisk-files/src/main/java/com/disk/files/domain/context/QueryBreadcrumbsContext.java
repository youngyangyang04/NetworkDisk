package com.disk.files.domain.context;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class QueryBreadcrumbsContext {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}
