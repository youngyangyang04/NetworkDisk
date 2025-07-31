package com.disk.share.domain.context;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
public class CreateShareContext {

    /**
     * 分享名称
     */
    private String shareName;

    /**
     * 分享类型
     */
    private Integer shareType;

    /**
     * 分享有效期
     */
    private String shareDayType;

    /**
     * 分享文件Id列表
     */
    private List<Long> shareFileIds;

}
