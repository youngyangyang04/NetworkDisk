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
public class QueryUploadedChunksContext {


    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
