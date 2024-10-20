package com.disk.files.domain.context;

import com.disk.files.domain.entity.FileDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class SaveFileContext {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 实体文件记录
     */
    private FileDO fileRecord;

    /**
     * 文件上传的物理路径
     */
    private String realPath;
}
