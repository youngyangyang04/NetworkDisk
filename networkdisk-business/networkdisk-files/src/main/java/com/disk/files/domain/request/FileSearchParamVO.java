package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileSearchParamVO {

    /**
     * 搜索的关键字
     */
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;

    /**
     * 文件类型，多个文件类型使用公用分隔符拼接
     */
    private String fileTypes;
}
