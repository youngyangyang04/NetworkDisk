package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类描述: 文件上传
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileUploadParamVO {

    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @NotNull(message = "文件的总大小不能为空")
    private Long totalSize;

    @NotBlank(message = "文件的父文件夹ID不能为空")
    private String parentId;

    @NotNull(message = "文件实体不能为空")
    private MultipartFile file;
}
