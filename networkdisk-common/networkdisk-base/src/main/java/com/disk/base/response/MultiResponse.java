package com.disk.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 响应
 *
 * @author weikunkun
 */
@Setter
@Getter
public class MultiResponse<T> extends BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> datas;

    public static <T> MultiResponse<T> of(List<T> datas) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setDatas(datas);
        return multiResponse;
    }

}
