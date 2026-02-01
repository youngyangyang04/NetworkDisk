package com.disk.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.user.domain.entity.UserDO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    UserDO findById(long id);

    /**
     * 根据昵称查询用户
     *
     * @param nickname
     * @return
     */
    UserDO findByNickname(@NotNull String nickname);

    /**
     * 根据手机号查询用户
     *
     * @param email
     * @return
     */
    UserDO findByEmail(@NotNull String email);
}
