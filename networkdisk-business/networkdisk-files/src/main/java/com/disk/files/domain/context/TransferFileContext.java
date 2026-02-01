package com.disk.files.domain.context;

import com.disk.files.domain.entity.UserFileDO;
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
public class TransferFileContext {

    /**
     * 要转移的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要转移的文件列表
     */
    private List<UserFileDO> prepareRecords;
}
