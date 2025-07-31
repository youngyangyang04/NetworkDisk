package com.disk.recycle.controller;

import com.disk.recycle.domain.request.DeleteFileParamVO;
import com.disk.recycle.domain.request.RestoreFileParamVO;
import com.disk.web.vo.Result;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@RestController
@RequestMapping("/api/v1/recycles")
public class RecycleController {

    @GetMapping("/list")
    public Result list() {
        return Result.success();
    }

    @GetMapping("/recycle/restore")
    public Result restore(@Validated @RequestBody RestoreFileParamVO restoreFileParam) {
        return Result.success();
    }

    @DeleteMapping("/recycle")
    public Result delete(@Validated @RequestBody DeleteFileParamVO deleteFileParam) {
        return Result.success();
    }

    
}
