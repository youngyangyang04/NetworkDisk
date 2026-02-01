package com.disk.api.files.request;

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
public class FolderCreateRequest extends BaseRequest {

    private String parentId;


    private String folderName;
}
