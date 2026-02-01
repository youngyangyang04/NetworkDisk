package com.disk.files.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileChunkUploadParamVO implements Serializable {


    private static final long serialVersionUID = 7006889430347866031L;

    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @NotNull(message = "总体的分片数不能为空")
    private Integer totalChunks;

    @NotNull(message = "当前分片的下标不能为空")
    private Integer chunkNumber;

    @NotNull(message = "当前分片的大小不能为空")
    private Long currentChunkSize;

    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @NotNull(message = "分片文件实体不能为空")
    private MultipartFile file;
}
