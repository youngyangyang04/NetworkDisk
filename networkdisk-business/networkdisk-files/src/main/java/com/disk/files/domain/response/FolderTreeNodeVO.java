package com.disk.files.domain.response;

import com.alibaba.fastjson.JSON;
import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class FolderTreeNodeVO {

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 文件夹id
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    /**
     * 父文件夹id
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    /**
     * 子节点集合
     */
    private List<FolderTreeNodeVO> children;

    public void print() {
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }

}
