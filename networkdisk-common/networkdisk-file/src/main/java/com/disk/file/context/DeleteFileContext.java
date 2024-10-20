package com.disk.file.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class DeleteFileContext {

    /**
     * 要删除的物理文件路径的集合
     */
    private List<String> realFilePathList;
}
