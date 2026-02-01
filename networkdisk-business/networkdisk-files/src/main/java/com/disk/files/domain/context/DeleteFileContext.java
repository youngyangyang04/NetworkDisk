package com.disk.files.domain.context;

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
     * 真实文件地址
     */
    private List<String> realFilePathList;
}
