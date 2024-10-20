package com.disk.files.domain.request;

import com.disk.base.request.BaseRequest;
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
public class CreateFolderParamVO extends BaseRequest {

    /**
     * 加密的父文件夹ID
     */
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}
