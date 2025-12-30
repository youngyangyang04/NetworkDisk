package com.disk.recycle.domain.service;

import com.disk.recycle.domain.entity.UserFileDO;
import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface RecycleService {
    List<UserFileDO> list(Long userId);

    void restore(Long userId, List<Long> fileIds);

    void hardDelete(Long userId, List<Long> fileIds);
}
