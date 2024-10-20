package com.disk.files.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.files.domain.context.QueryFileContext;
import com.disk.files.domain.entity.UserFileDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper
public interface UserFileMapper extends BaseMapper<UserFileDO> {

    /**
     * 列举用户文件列表
     * @param context
     * @return
     */
    List<UserFileDO> listUserFiles(@Param("param") QueryFileContext context);

}
