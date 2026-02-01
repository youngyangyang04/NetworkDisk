package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: 秒传请求VO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class SecUploadFileParamVO {

    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}
