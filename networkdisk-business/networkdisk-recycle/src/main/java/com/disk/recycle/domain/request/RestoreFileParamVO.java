package com.disk.recycle.domain.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class RestoreFileParamVO {

    /**
     * 复原的文件id列表
     */
    @NotEmpty(message = "未选择复原文件")
    private List<String> fileIds;
}
