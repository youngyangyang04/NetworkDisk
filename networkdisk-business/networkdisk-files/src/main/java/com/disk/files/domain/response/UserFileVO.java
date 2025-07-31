package com.disk.files.domain.response;

import com.disk.files.infrastructure.serializer.Date2StringSerializer;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class UserFileVO implements Serializable {

    private static final long serialVersionUID = 1L;


    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    /**
     * 父文件夹ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件大小描述
     */
    private String fileSizeDesc;

    /**
     * 文件夹标识 0 否 1 是
     */
    private Integer folderFlag;

    /**
     * 文件类型 1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv
     */
    private Integer fileType;

    /**
     * 文件更新时间
     */
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;

}
