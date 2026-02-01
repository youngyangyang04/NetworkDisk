package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class TransferFileParamVO {

    /**
     * 要转移的文件ID集合，多个使用公用分隔符隔开
     */
    @NotEmpty(message = "请选择要转移的文件")
    private List<String> fileIds;

    /**
     * 要转移到的目标文件夹的ID
     */
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;

}
