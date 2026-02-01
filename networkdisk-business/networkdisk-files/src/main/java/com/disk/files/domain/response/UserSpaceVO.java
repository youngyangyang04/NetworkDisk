package com.disk.files.domain.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserSpaceVO {

    /**
     * 使用量
     */
    private Long useSpace;

    /**
     * 总量
     */
    private Long totalSpace;
}
