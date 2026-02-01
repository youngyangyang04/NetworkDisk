package com.disk.share.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@AllArgsConstructor
public enum ShareStatusEnum {

    NORMAL(0, "正常状态"),
    FILE_DELETED(1, "有文件被删除");

    private final Integer code;

    private final String desc;
}
