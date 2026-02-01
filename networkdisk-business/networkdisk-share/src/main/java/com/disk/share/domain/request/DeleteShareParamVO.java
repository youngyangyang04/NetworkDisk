package com.disk.share.domain.request;

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
public class DeleteShareParamVO {

    @NotBlank(message = "请选中待删除的分享信息")
    private String shareId;


}
