package com.disk.share.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.response.ShareFileInfoVO;
import jakarta.servlet.http.HttpServletResponse;

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

    /**
     * Query a share info by share id for public link access.
     *
     * @param shareId share id
     * @return share info, null when not exists
     */
    ShareDO getByShareId(Long shareId);

    /**
     * Validate extraction code for a share link.
     *
     * @param shareId share id
     * @param shareCode extraction code
     * @return true when passed
     */
    boolean checkShareCode(Long shareId, String shareCode);

    /**
     * List files included in a share link.
     *
     * @param shareId share id
     * @param shareCode extraction code (optional for public share)
     * @return files
     */
    List<ShareFileInfoVO> listShareFiles(Long shareId, String shareCode);

    /**
     * Download a file from a share link.
     *
     * @param shareId share id
     * @param fileId user file id in share_file
     * @param shareCode extraction code (optional for public share)
     * @param response http response
     */
    void downloadShareFile(Long shareId, Long fileId, String shareCode, HttpServletResponse response);
}
