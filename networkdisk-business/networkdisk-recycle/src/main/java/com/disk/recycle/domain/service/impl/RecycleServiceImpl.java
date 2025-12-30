package com.disk.recycle.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.recycle.domain.entity.UserFileDO;
import com.disk.recycle.domain.service.RecycleService;
import com.disk.recycle.infrastructure.mapper.UserFileMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecycleServiceImpl extends ServiceImpl<UserFileMapper, UserFileDO> implements RecycleService {

    @Override
    public List<UserFileDO> list(Long userId) {
        return baseMapper.listDeletedByUser(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void restore(Long userId, List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        baseMapper.restoreByIds(userId, fileIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void hardDelete(Long userId, List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        baseMapper.hardDeleteByIds(userId, fileIds);
    }
}
