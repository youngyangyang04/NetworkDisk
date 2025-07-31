package com.disk.share.domain.entity.convertor;

import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.request.CreateShareParamVO;
import com.disk.share.domain.request.DeleteShareParamVO;
import com.disk.share.domain.response.UserShareInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring")
public interface ShareConvertor {

    List<UserShareInfoVO> mapToVoList(List<ShareDO> shareDOList);

    @Mapping(
            target = "shareFileIds",
            expression = "java(createShareParam.getShareFileIds() == null ? null : " +
                    "createShareParam.getShareFileIds().stream().map(com.disk.base.utils.IdUtil::decrypt).toList())"
    )
    CreateShareContext createShareParamToCreateShareContext(CreateShareParamVO createShareParam);

    @Mapping(target = "createTime", source = "gmtCreate")
    UserShareInfoVO mapToVo(ShareDO shareDO);

    @Mapping(
            target = "shareId",
            expression = "java(com.disk.base.utils.IdUtil.decrypt(deleteShareParam.getShareId()))"
    )
    DeleteShareContext deleteParamToDeleteContext(DeleteShareParamVO deleteShareParam);
}
