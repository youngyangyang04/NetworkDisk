package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class QueryUploadedChunkListParamVO {

    /**
     * 文件唯一标识符
     */
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;
}
