package com.disk.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页响应
 *
 * @author weikunkun
 */
@Setter
@Getter
public class PageResponse<T> extends MultiResponse<T> {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页结果数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总数
     */
    private int total;

    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize) {
        PageResponse<T> multiResponse = new PageResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setDatas(datas);
        multiResponse.setTotal(total);
        multiResponse.setPageSize(pageSize);
        multiResponse.setTotalPage((pageSize + total - 1) / pageSize);
        return multiResponse;
    }
}
