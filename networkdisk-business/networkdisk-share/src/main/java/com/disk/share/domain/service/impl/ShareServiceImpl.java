package com.disk.share.domain.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.enums.DeleteEnum;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.entity.ShareFileDO;
import com.disk.share.domain.service.ShareFileService;
import com.disk.share.domain.service.ShareService;
import com.disk.share.exception.ShareErrorCode;
import com.disk.share.exception.ShareException;
import com.disk.share.infrastructure.enums.ShareDayTypeEnum;
import com.disk.share.infrastructure.enums.ShareStatusEnum;
import com.disk.share.infrastructure.mapper.ShareMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareDO> implements ShareService {

    @Autowired
    private ShareFileService shareFileService;


    @Override
    public List<ShareDO> listUserShareInfos(Long create_user) {
        QueryWrapper<ShareDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("create_user", create_user);
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        return this.list(queryWrapper);
    }

    /**
     * 熙增分享信息
     *
     * @param context
     * @returnx
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUserShareInfo(CreateShareContext context) {
        List<Long> shareFileIds = context.getShareFileIds();
        log.info("fileIdList: [{}]", shareFileIds);
        ShareDO shareDO = assembleShareInfo(context);
        List<ShareFileDO> shareFileDOList = shareFileIds.stream().map(fileId -> {
            ShareFileDO shareFileDO = new ShareFileDO();
            shareFileDO.setFileId(fileId);
            shareFileDO.setShareId(shareDO.getId());
            shareFileDO.setCreateUser(shareDO.getCreateUser());
            shareFileDO.setId(IdUtil.get());
            return shareFileDO;
        }).collect(Collectors.toList());
        save(shareDO);
        shareFileService.saveBatch(shareFileDOList);
        return shareDO.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserShare(DeleteShareContext context) {
        Long shareId = context.getShareId();
        if (EmptyUtil.isEmpty(shareId)) {
            throw new ShareException(ShareErrorCode.INVALID_ARGS);
        }
        UpdateWrapper<ShareDO> updateShareWrapper = Wrappers.update();
        updateShareWrapper.in("id", shareId);
        updateShareWrapper.set("deleted", DeleteEnum.YES.getCode());
        update(updateShareWrapper);

        UpdateWrapper<ShareFileDO> updateShareFileWrapper = Wrappers.update();
        updateShareFileWrapper.in("share_id", shareId);
        updateShareFileWrapper.set("deleted", DeleteEnum.YES.getCode());
        shareFileService.update(updateShareFileWrapper);
    }


    private ShareDO assembleShareInfo(CreateShareContext context) {
        Long shareId = IdUtil.get();
        Integer shareDay = ShareDayTypeEnum.getDayByType(context.getShareDayType());
        ShareDO shareDO = new ShareDO();
        shareDO.setId(shareId);
        shareDO.setShareDay(shareDay);
        shareDO.setCreateUser(UserIdUtil.get());
        shareDO.setShareType(context.getShareType());
        shareDO.setShareDayType(context.getShareDayType());
        shareDO.setShareName(context.getShareName());
        shareDO.setShareEndTime(DateUtil.offsetDay(new Date(), shareDay));
        shareDO.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        shareDO.setShareCode(createShareCode());
        //TODO  fix me url地址修改
        shareDO.setShareUrl("xxxxx-" +  RandomStringUtils.randomAlphabetic(4).toLowerCase());
        return shareDO;
    }


    /**
     * 生成分享码
     * @return
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }
}
