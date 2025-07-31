package com.disk.share.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateShareParamVO {

    /**
     * 分享名称
     */
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    /**
     * 分享类型
     */
    @NotNull(message = "请选择分享类型")
    private Integer shareType;

    /**
     * 分享有效期
     */
    @NotBlank(message = "请选择分享有效期")
    private String shareDayType;

    /**
     * 分享文件Id列表
     */
    @NotNull(message = "请选择分享文件")
    private List<String> shareFileIds;

}
