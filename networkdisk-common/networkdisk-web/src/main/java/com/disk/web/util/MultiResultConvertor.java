package com.disk.web.util;

import com.disk.base.response.PageResponse;
import com.disk.web.vo.MultiResult;

import static com.disk.base.response.ResponseCode.SUCCESS;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class MultiResultConvertor {

    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        MultiResult<T> multiResult = new MultiResult<T>(true, SUCCESS.name(), SUCCESS.name(),
                pageResponse.getDatas(), pageResponse.getTotal(), pageResponse.getCurrentPage(), pageResponse.getPageSize());
        return multiResult;
    }
}
