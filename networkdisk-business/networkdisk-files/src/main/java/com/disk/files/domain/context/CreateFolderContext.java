package com.disk.files.domain.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class CreateFolderContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 2054791483504271201L;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;
}
