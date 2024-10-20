package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class DeleteFileParamVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2479458304929418333L;

    @NotEmpty(message = "请选择要删除的文件信息")
    private List<String> fileIds;
}
