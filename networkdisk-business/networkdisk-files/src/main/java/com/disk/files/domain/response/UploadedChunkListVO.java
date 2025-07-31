package com.disk.files.domain.response;

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
public class UploadedChunkListVO {


    /**
     * 已上传的分片编号列表
     */
    private List<Integer> uploadedChunks;
}
