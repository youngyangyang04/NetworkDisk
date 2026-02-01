package com.disk.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@AllArgsConstructor
public enum DeleteEnum {

    /**
     * 未删除
     */
    NO(0),
    /**
     * 已删除
     */
    YES(1);

    private final Integer code;


}
