package com.disk.files.infrastructure.es.entity;

import lombok.Data;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.util.Date;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
@IndexName("user_file_index")
public class UserFileESEntity {

    /** 主键ID（对应 ES _id） */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;

    /** 用户ID */
    @IndexField(fieldType = FieldType.KEYWORD, value = "user_id")
    private Long userId;

    /** 父级文件夹ID */
    @IndexField(fieldType = FieldType.KEYWORD, value = "parent_id")
    private Long parentId;

    /** 真实文件ID（物理文件） */
    @IndexField(fieldType = FieldType.KEYWORD, value = "real_file_id")
    private Long realFileId;

    /** 文件名（分词、支持搜索、高亮） */
    @HighLight(mappingField = "filename")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, value = "filename")
    private String filename;

    /** 文件夹标识（1=文件夹，0=文件） */
    @IndexField(fieldType = FieldType.INTEGER, value = "folder_flag")
    private Integer folderFlag;

    /** 文件大小描述（如“12MB”） */
    @IndexField(fieldType = FieldType.KEYWORD, value =  "file_size_desc")
    private String fileSizeDesc;

    /** 文件类型（如“pdf”、“jpg”、“docx”等） */
    @IndexField(fieldType = FieldType.KEYWORD, value = "file_type")
    private Integer fileType;

    /** 创建人 */
    @IndexField(fieldType = FieldType.KEYWORD, value = "create_user")
    private String createUser;

    /** 创建时间 */
    @IndexField(fieldType = FieldType.DATE, value = "gmt_create")
    private Date gmtCreate;

    /** 修改时间 */
    @IndexField(fieldType = FieldType.DATE, value = "gmt_modified")
    private Date gmtModified;

    /** 更新人 */
    @IndexField(fieldType = FieldType.KEYWORD, value = "update_user")
    private String updateUser;

    /** 删除标志（0=正常，1=已删除） */
    @IndexField(fieldType = FieldType.BOOLEAN, value = "deleted")
    private Boolean deleted;

    /** 乐观锁版本号 */
    @IndexField(fieldType = FieldType.INTEGER, value = "lock_version")
    private Integer lockVersion;
}
