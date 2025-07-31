package com.disk.files.domain.response;

import com.disk.files.domain.entity.UserFileDO;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class BreadcrumbVO {

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    private String name;

    /**
     * 实体转换
     *
     * @param record
     * @return
     */
    public static BreadcrumbVO transfer(UserFileDO record) {

        BreadcrumbVO vo = new BreadcrumbVO();
        if (Objects.nonNull(record)) {
            vo.setId(record.getId());
            vo.setParentId(record.getParentId());
            vo.setName(record.getFilename());
        }

        return vo;
    }


}
