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
public class CopyFileParamVO {

    /**
     * 要复制的文件ID集合，多个使用公用分隔符隔开
     */
    @NotBlank(message = "请选择要复制的文件")
    private String fileIds;

    /**
     * 要转移到的目标文件夹的ID
     */
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;
}
