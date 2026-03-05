package com.disk.share.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.share.domain.entity.UserFileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFileMapper extends BaseMapper<UserFileDO> {
}
