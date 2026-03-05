package com.disk.recycle.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.recycle.domain.entity.UserFileDO;
import com.disk.recycle.domain.service.RecycleService;
import com.disk.recycle.infrastructure.mapper.UserFileMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<Long> restoreIds = collectOperationIds(userId, fileIds);
        if (CollectionUtils.isEmpty(restoreIds)) {
            return;
        }
        baseMapper.restoreByIds(userId, restoreIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void hardDelete(Long userId, List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        List<Long> hardDeleteIds = collectOperationIds(userId, fileIds);
        if (CollectionUtils.isEmpty(hardDeleteIds)) {
            return;
        }
        baseMapper.hardDeleteByIds(userId, hardDeleteIds);
    }

    /**
     * 展开操作 ID：
     * 1. 过滤出当前用户回收站中存在的选中记录
     * 2. 如果选中的是文件夹，递归补齐其所有已删除子孙节点
     */
    private List<Long> collectOperationIds(Long userId, List<Long> selectedIds) {
        List<UserFileDO> deletedRecords = baseMapper.listDeletedByUser(userId);
        if (CollectionUtils.isEmpty(deletedRecords)) {
            return new ArrayList<>();
        }

        Set<Long> deletedIdSet = deletedRecords.stream()
                .map(UserFileDO::getId)
                .collect(Collectors.toSet());

        Set<Long> operationIdSet = selectedIds.stream()
                .filter(deletedIdSet::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (operationIdSet.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<UserFileDO>> childrenMap = new HashMap<>();
        for (UserFileDO record : deletedRecords) {
            childrenMap.computeIfAbsent(record.getParentId(), key -> new ArrayList<>()).add(record);
        }

        ArrayDeque<Long> queue = new ArrayDeque<>(operationIdSet);
        while (!queue.isEmpty()) {
            Long parentId = queue.poll();
            List<UserFileDO> children = childrenMap.get(parentId);
            if (CollectionUtils.isEmpty(children)) {
                continue;
            }
            for (UserFileDO child : children) {
                if (operationIdSet.add(child.getId())) {
                    queue.offer(child.getId());
                }
            }
        }
        return new ArrayList<>(operationIdSet);
    }
}
