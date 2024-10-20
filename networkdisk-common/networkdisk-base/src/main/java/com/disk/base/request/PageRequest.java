package com.disk.base.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页请求
 *
 * @author weikunkun
 */
@Setter
@Getter
public class PageRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页结果数
     */
    private int pageSize;
}
