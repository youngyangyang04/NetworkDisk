package com.disk.files.domain.response;

import com.disk.files.infrastructure.serializer.Date2StringSerializer;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FileSearchVO {

    /**
     * 文件id
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;

    /**
     * 父文件id
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    /**
     * 父文件夹名称
     */
    private String parentFilename;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件大小描述
     */
    private String fileSizeDesc;

    /**
     * 文件夹标识
     */
    private Integer folderFlag;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 文件更新时间
     */
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;
}
