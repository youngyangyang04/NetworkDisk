package com.disk.files.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@AllArgsConstructor
public enum MergeFlagEnum {

    /**
     * 不需要合并
     */
    NOT_READY(0),

    /**
     * 需要合并
     */
    READY(1);

    private final Integer code;
}
