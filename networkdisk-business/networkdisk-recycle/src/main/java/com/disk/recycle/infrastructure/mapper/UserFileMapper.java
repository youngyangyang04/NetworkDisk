package com.disk.recycle.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.recycle.domain.entity.UserFileDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFileMapper extends BaseMapper<UserFileDO> {

    List<UserFileDO> listDeletedByUser(@Param("userId") Long userId);

    int restoreByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    int hardDeleteByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}





