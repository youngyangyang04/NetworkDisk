package com.disk.share.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.ShareDO;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public interface ShareService extends IService<ShareDO> {

    /**
     * 列举用户分享链接信息
     * @param aLong
     * @return
     */
    List<ShareDO> listUserShareInfos(Long aLong);

    /**
     * 新增分享信息
     * @param context
     * @return
     */
    Long addUserShareInfo(CreateShareContext context);

    void deleteUserShare(DeleteShareContext context);
}
