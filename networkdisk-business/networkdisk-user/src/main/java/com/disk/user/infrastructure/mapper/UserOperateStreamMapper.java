package com.disk.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.user.domain.entity.UserOperateSteamDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类描述: 用户操作流水表 Mapper 接口
 *
 * @author weikunkun
 */
@Mapper
public interface UserOperateStreamMapper extends BaseMapper<UserOperateSteamDO> {


}
