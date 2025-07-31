package com.disk.files.domain.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class FileChunkUploadVO {

    /**
     * 是否需要合并文件 0 不需要 1 需要
     */
    private Integer mergeFlag;
}
