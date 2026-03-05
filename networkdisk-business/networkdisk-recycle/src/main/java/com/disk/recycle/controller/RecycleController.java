package com.disk.recycle.controller;

import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.recycle.domain.entity.UserFileDO;
import com.disk.recycle.domain.service.RecycleService;
import com.disk.recycle.domain.request.DeleteFileParamVO;
import com.disk.recycle.domain.request.RestoreFileParamVO;
import com.disk.web.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@RestController
@RequestMapping("/api/v1/recycles")
public class RecycleController {

    @GetMapping("/list")
    public Result<java.util.List<UserFileDO>> list(@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize) {
        Long userId = UserIdUtil.get();
        // 暂不做分页，按修改时间倒序返回
        return Result.success(recycleService.list(userId));
    }

    @PutMapping("/recycle/restore")
    public Result<Boolean> restore(@Validated @RequestBody RestoreFileParamVO restoreFileParam) {
        Long userId = UserIdUtil.get();
        recycleService.restore(userId, parseFileIds(restoreFileParam.getFileIds()));
        return Result.success(true);
    }

    @DeleteMapping("/recycle")
    public Result<Boolean> delete(@Validated @RequestBody DeleteFileParamVO deleteFileParam) {
        Long userId = UserIdUtil.get();
        recycleService.hardDelete(userId, parseFileIds(deleteFileParam.getFileIds()));
        return Result.success(true);
    }

    /**
     * 兼容两种 ID 传参：
     * 1. 明文 long（回收站当前实现）
     * 2. 加密字符串（兼容历史前端或其他调用方）
     */
    private List<Long> parseFileIds(List<String> fileIds) {
        return fileIds.stream().map(this::parseFileId).toList();
    }

    private Long parseFileId(String fileId) {
        try {
            return Long.valueOf(fileId);
        } catch (NumberFormatException ignore) {
            return IdUtil.decrypt(fileId);
        }
    }

    @Autowired
    private RecycleService recycleService;
}
