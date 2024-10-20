package com.disk.user.domain.entity.convertor;

import com.disk.api.user.response.data.UserInfo;
import com.disk.user.domain.entity.UserDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * 类描述: User转换
 *
 * @author weikunkun
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    @Mapping(source = "request.id", target = "userId")
    public UserInfo mapToVo(UserDO request);

    /**
     * 转换为实体
     *
     * @param request
     * @return
     */
    @Mapping(source = "request.userId", target = "id")
    public UserDO mapToEntity(UserInfo request);
}
