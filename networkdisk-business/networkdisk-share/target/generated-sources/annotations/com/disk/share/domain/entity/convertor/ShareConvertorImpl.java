package com.disk.share.domain.entity.convertor;

import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.request.CreateShareParamVO;
import com.disk.share.domain.request.DeleteShareParamVO;
import com.disk.share.domain.response.UserShareInfoVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-06T15:28:55+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ShareConvertorImpl implements ShareConvertor {

    @Override
    public List<UserShareInfoVO> mapToVoList(List<ShareDO> shareDOList) {
        if ( shareDOList == null ) {
            return null;
        }

        List<UserShareInfoVO> list = new ArrayList<UserShareInfoVO>( shareDOList.size() );
        for ( ShareDO shareDO : shareDOList ) {
            list.add( mapToVo( shareDO ) );
        }

        return list;
    }

    @Override
    public CreateShareContext createShareParamToCreateShareContext(CreateShareParamVO createShareParam) {
        if ( createShareParam == null ) {
            return null;
        }

        CreateShareContext createShareContext = new CreateShareContext();

        if ( createShareParam.getShareName() != null ) {
            createShareContext.setShareName( createShareParam.getShareName() );
        }
        if ( createShareParam.getShareType() != null ) {
            createShareContext.setShareType( createShareParam.getShareType() );
        }
        if ( createShareParam.getShareDayType() != null ) {
            createShareContext.setShareDayType( createShareParam.getShareDayType() );
        }

        createShareContext.setShareFileIds( createShareParam.getShareFileIds() == null ? null : createShareParam.getShareFileIds().stream().map(com.disk.base.utils.IdUtil::decrypt).toList() );

        return createShareContext;
    }

    @Override
    public UserShareInfoVO mapToVo(ShareDO shareDO) {
        if ( shareDO == null ) {
            return null;
        }

        UserShareInfoVO userShareInfoVO = new UserShareInfoVO();

        if ( shareDO.getGmtCreate() != null ) {
            userShareInfoVO.setCreateTime( shareDO.getGmtCreate() );
        }
        if ( shareDO.getId() != null ) {
            userShareInfoVO.setId( shareDO.getId() );
        }
        if ( shareDO.getShareName() != null ) {
            userShareInfoVO.setShareName( shareDO.getShareName() );
        }
        if ( shareDO.getShareType() != null ) {
            userShareInfoVO.setShareType( shareDO.getShareType() );
        }
        if ( shareDO.getShareDayType() != null ) {
            userShareInfoVO.setShareDayType( shareDO.getShareDayType() );
        }
        if ( shareDO.getShareDay() != null ) {
            userShareInfoVO.setShareDay( shareDO.getShareDay() );
        }
        if ( shareDO.getShareEndTime() != null ) {
            userShareInfoVO.setShareEndTime( shareDO.getShareEndTime() );
        }
        if ( shareDO.getShareUrl() != null ) {
            userShareInfoVO.setShareUrl( shareDO.getShareUrl() );
        }
        if ( shareDO.getShareCode() != null ) {
            userShareInfoVO.setShareCode( shareDO.getShareCode() );
        }
        if ( shareDO.getShareStatus() != null ) {
            userShareInfoVO.setShareStatus( shareDO.getShareStatus() );
        }
        if ( shareDO.getCreateUser() != null ) {
            userShareInfoVO.setCreateUser( shareDO.getCreateUser() );
        }

        return userShareInfoVO;
    }

    @Override
    public DeleteShareContext deleteParamToDeleteContext(DeleteShareParamVO deleteShareParam) {
        if ( deleteShareParam == null ) {
            return null;
        }

        DeleteShareContext deleteShareContext = new DeleteShareContext();

        deleteShareContext.setShareId( com.disk.base.utils.IdUtil.decrypt(deleteShareParam.getShareId()) );

        return deleteShareContext;
    }
}
