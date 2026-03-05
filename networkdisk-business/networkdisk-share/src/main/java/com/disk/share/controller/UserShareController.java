package com.disk.share.controller;

import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.convertor.ShareConvertor;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.request.CreateShareParamVO;
import com.disk.share.domain.request.DeleteShareParamVO;
import com.disk.share.domain.request.ShareCodeCheckParamVO;
import com.disk.share.domain.response.ShareFileInfoVO;
import com.disk.share.domain.response.UserShareInfoVO;
import com.disk.share.domain.service.ShareService;
import com.disk.share.exception.ShareErrorCode;
import com.disk.web.annotation.LoginIgnore;
import com.disk.web.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/shares")
public class UserShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private ShareConvertor shareConvertor;

    @GetMapping("/list")
    public Result<List<UserShareInfoVO>> listUserShares() {
        List<ShareDO> list = shareService.listUserShareInfos(UserIdUtil.get());
        List<UserShareInfoVO> shareInfoVOList = shareConvertor.mapToVoList(list);
        return Result.success(shareInfoVOList);
    }

    @GetMapping("/share")
    public Result<UserShareInfoVO> getUserShare(@RequestParam("shareId") String shareId) {
        Long id = IdUtil.decrypt(shareId);
        ShareDO shareDO = shareService.getByShareId(id);
        if (shareDO == null) {
            return Result.error(ShareErrorCode.SHARE_NOT_FOUND);
        }
        UserShareInfoVO infoVO = shareConvertor.mapToVo(shareDO);
        return Result.success(infoVO);
    }

    @LoginIgnore
    @GetMapping("/share/simple")
    public Result<UserShareInfoVO> getSimpleShare(@RequestParam("shareId") String shareId) {
        Long id = IdUtil.decrypt(shareId);
        ShareDO shareDO = shareService.getByShareId(id);
        if (shareDO == null) {
            return Result.error(ShareErrorCode.SHARE_NOT_FOUND);
        }
        UserShareInfoVO infoVO = shareConvertor.mapToVo(shareDO);
        // Never expose extraction code to anonymous visitors.
        infoVO.setShareCode(null);
        return Result.success(infoVO);
    }

    @LoginIgnore
    @PostMapping("/share/code/check")
    public Result<Boolean> checkShareCode(@Validated @RequestBody ShareCodeCheckParamVO checkParam) {
        Long shareId = IdUtil.decrypt(checkParam.getShareId());
        return Result.success(shareService.checkShareCode(shareId, checkParam.getShareCode()));
    }

    @LoginIgnore
    @GetMapping("/share/files")
    public Result<List<ShareFileInfoVO>> listShareFiles(@RequestParam("shareId") String shareId,
                                                         @RequestParam(value = "shareCode", required = false) String shareCode) {
        Long id = IdUtil.decrypt(shareId);
        List<ShareFileInfoVO> files = shareService.listShareFiles(id, shareCode);
        return Result.success(files);
    }

    @LoginIgnore
    @GetMapping("/share/file/download")
    public void downloadShareFile(@RequestParam("shareId") String shareId,
                                  @RequestParam("fileId") String fileId,
                                  @RequestParam(value = "shareCode", required = false) String shareCode,
                                  HttpServletResponse response) {
        shareService.downloadShareFile(IdUtil.decrypt(shareId), IdUtil.decrypt(fileId), shareCode, response);
    }

    @PostMapping("/share")
    public Result<String> addUserShare(@Validated @RequestBody CreateShareParamVO createShareParam) {
        log.info("createShareParam: [{}]", createShareParam.toString());
        CreateShareContext context = shareConvertor.createShareParamToCreateShareContext(createShareParam);
        Long shareId = shareService.addUserShareInfo(context);
        return Result.success(IdUtil.encrypt(shareId));
    }

    @DeleteMapping("/share")
    public Result<String> deleteUserShare(@Validated @RequestBody DeleteShareParamVO deleteShareParam) {
        DeleteShareContext context = shareConvertor.deleteParamToDeleteContext(deleteShareParam);
        shareService.deleteUserShare(context);
        return Result.success(deleteShareParam.getShareId());
    }
}
