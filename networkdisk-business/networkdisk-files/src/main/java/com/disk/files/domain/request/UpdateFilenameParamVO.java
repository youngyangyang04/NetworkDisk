package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateFilenameParamVO implements Serializable {


    @Serial
    private static final long serialVersionUID = -9030006628129809829L;


    @NotBlank(message = "更新的文件ID不能为空")
    private String fileId;

    @NotBlank(message = "新的文件名称不能为空")
    private String newFilename;
}
