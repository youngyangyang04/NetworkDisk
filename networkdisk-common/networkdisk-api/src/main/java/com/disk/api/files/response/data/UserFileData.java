package com.disk.api.files.response.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserFileData implements Serializable {


    @Serial
    private static final long serialVersionUID = 4073971751074292803L;
    /**
     * 文件记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 上级文件夹ID,顶级文件夹为0
     */
    private Long parentId;

    /**
     * 真实文件id
     */
    private Long realFileId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 是否是文件夹 （0 否 1 是）
     */
    private Integer folderFlag;

    /**
     * 文件大小展示字符
     */
    private String fileSizeDesc;

    /**
     * 文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     */
    private Integer fileType;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private Long createUser;
}
