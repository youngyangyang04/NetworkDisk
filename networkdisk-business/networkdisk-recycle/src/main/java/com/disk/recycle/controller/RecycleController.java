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

    @GetMapping("/recycle/restore")
    public Result<Boolean> restore(@Validated @RequestBody RestoreFileParamVO restoreFileParam) {
        Long userId = UserIdUtil.get();
        recycleService.restore(userId, restoreFileParam.getFileIds().stream().map(IdUtil::decrypt).toList());
        return Result.success(true);
    }

    @DeleteMapping("/recycle")
    public Result<Boolean> delete(@Validated @RequestBody DeleteFileParamVO deleteFileParam) {
        Long userId = UserIdUtil.get();
        recycleService.hardDelete(userId, deleteFileParam.getFileIds().stream().map(IdUtil::decrypt).toList());
        return Result.success(true);
    }

    @Autowired
    private RecycleService recycleService;
}
