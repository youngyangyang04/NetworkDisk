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
public class SecUploadFileContext {

    private Long parentId;

    private String filename;

    private String identifier;

    private Long userId;
}
