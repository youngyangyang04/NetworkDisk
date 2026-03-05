package com.disk.share.domain.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.base.enums.DeleteEnum;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.HttpUtil;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.UserIdUtil;
import com.disk.delayqueue.DelayQueueHolder;
import com.disk.share.domain.context.CreateShareContext;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.entity.FileDO;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.entity.ShareFileDO;
import com.disk.share.domain.entity.UserFileDO;
import com.disk.share.job.delay.DeleteShareInfoMessage;
import com.disk.share.domain.response.ShareFileInfoVO;
import com.disk.share.domain.service.ShareFileService;
import com.disk.share.domain.service.ShareService;
import com.disk.share.exception.ShareErrorCode;
import com.disk.share.exception.ShareException;
import com.disk.share.infrastructure.constant.ShareConstant;
import com.disk.share.infrastructure.enums.ShareDayTypeEnum;
import com.disk.share.infrastructure.enums.ShareStatusEnum;
import com.disk.share.infrastructure.mapper.FileMapper;
import com.disk.share.infrastructure.mapper.ShareMapper;
import com.disk.share.infrastructure.mapper.UserFileMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Share domain service implementation.
 */
@Slf4j
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareDO> implements ShareService {

    private static final Integer PRIVATE_SHARE_TYPE = 1;
    private static final Integer FOLDER_FLAG = 1;

    @Autowired
    private ShareFileService shareFileService;

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private DelayQueueHolder delayQueueHolder;

    @Value("${com.disk.share.access-url-prefix:http://localhost:5173/share}")
    private String shareAccessUrlPrefix;

    @Override
    public List<ShareDO> listUserShareInfos(Long createUser) {
        QueryWrapper<ShareDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("create_user", createUser);
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        return this.list(queryWrapper);
    }

    @Override
    public ShareDO getByShareId(Long shareId) {
        if (EmptyUtil.isEmpty(shareId)) {
            throw new ShareException(ShareErrorCode.INVALID_ARGS);
        }
        QueryWrapper<ShareDO> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", shareId);
        queryWrapper.eq("deleted", DeleteEnum.NO.getCode());
        return this.getOne(queryWrapper);
    }

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
        scheduleShareExpireDeleteJob(shareDO);
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
        removeShareExpireDeleteJob(shareId);
    }

    @Override
    public boolean checkShareCode(Long shareId, String shareCode) {
        ShareDO shareDO = assertShareAvailable(shareId);
        validateShareCodeIfNeeded(shareDO, shareCode);
        return true;
    }

    @Override
    public List<ShareFileInfoVO> listShareFiles(Long shareId, String shareCode) {
        ShareDO shareDO = assertShareAvailable(shareId);
        validateShareCodeIfNeeded(shareDO, shareCode);

        QueryWrapper<ShareFileDO> mappingQuery = Wrappers.query();
        mappingQuery.eq("share_id", shareId);
        mappingQuery.eq("deleted", DeleteEnum.NO.getCode());
        mappingQuery.orderByDesc("gmt_create");
        List<ShareFileDO> mappings = shareFileService.list(mappingQuery);
        if (EmptyUtil.isEmpty(mappings)) {
            return List.of();
        }

        List<Long> fileIds = mappings.stream().map(ShareFileDO::getFileId).distinct().toList();
        List<UserFileDO> fileRecords = userFileMapper.selectBatchIds(fileIds);
        if (EmptyUtil.isEmpty(fileRecords)) {
            return List.of();
        }
        Map<Long, UserFileDO> fileRecordMap = fileRecords.stream()
                .filter(record -> Objects.equals(record.getDeleted(), DeleteEnum.NO.getCode()))
                .collect(Collectors.toMap(UserFileDO::getId, record -> record, (left, right) -> left));

        return mappings.stream()
                .map(mapping -> fileRecordMap.get(mapping.getFileId()))
                .filter(Objects::nonNull)
                .map(this::toShareFileInfoVO)
                .toList();
    }

    @Override
    public void downloadShareFile(Long shareId, Long fileId, String shareCode, HttpServletResponse response) {
        ShareDO shareDO = assertShareAvailable(shareId);
        validateShareCodeIfNeeded(shareDO, shareCode);

        QueryWrapper<ShareFileDO> mappingQuery = Wrappers.query();
        mappingQuery.eq("share_id", shareId);
        mappingQuery.eq("file_id", fileId);
        mappingQuery.eq("deleted", DeleteEnum.NO.getCode());
        ShareFileDO mapping = shareFileService.getOne(mappingQuery);
        if (mapping == null) {
            throw new ShareException(ShareErrorCode.SHARE_FILE_NOT_FOUND);
        }

        UserFileDO userFileDO = userFileMapper.selectById(fileId);
        if (userFileDO == null || Objects.equals(userFileDO.getDeleted(), DeleteEnum.YES.getCode())) {
            throw new ShareException(ShareErrorCode.SHARE_FILE_NOT_FOUND);
        }
        if (Objects.equals(userFileDO.getFolderFlag(), FOLDER_FLAG)) {
            throw new ShareException(ShareErrorCode.INVALID_ARGS);
        }

        FileDO fileDO = fileMapper.selectById(userFileDO.getRealFileId());
        if (fileDO == null || Objects.equals(fileDO.getDeleted(), DeleteEnum.YES.getCode())) {
            throw new ShareException(ShareErrorCode.SHARE_FILE_NOT_FOUND);
        }

        Path realFilePath = Paths.get(fileDO.getRealPath());
        if (!Files.exists(realFilePath)) {
            throw new ShareException(ShareErrorCode.SHARE_FILE_NOT_FOUND);
        }

        prepareDownloadResponse(response, userFileDO.getFilename(), fileDO.getFileSize());
        try (InputStream inputStream = Files.newInputStream(realFilePath);
             OutputStream outputStream = response.getOutputStream()) {
            inputStream.transferTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new ShareException("Share file download failed", e, ShareErrorCode.SHARE_FILE_NOT_FOUND);
        }
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
        shareDO.setShareEndTime(shareDay < 0 ? null : DateUtil.offsetDay(new Date(), shareDay));
        shareDO.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        shareDO.setShareCode(createShareCode());
        shareDO.setShareUrl(buildShareUrl(shareId));
        return shareDO;
    }

    private ShareDO assertShareAvailable(Long shareId) {
        ShareDO shareDO = getByShareId(shareId);
        if (shareDO == null) {
            throw new ShareException(ShareErrorCode.SHARE_NOT_FOUND);
        }

        if (!isPermanentShare(shareDO) && shareDO.getShareEndTime() != null && shareDO.getShareEndTime().before(new Date())) {
            throw new ShareException(ShareErrorCode.SHARE_EXPIRED);
        }
        return shareDO;
    }

    private boolean isPermanentShare(ShareDO shareDO) {
        if (Objects.equals(shareDO.getShareDay(), -1)) {
            return true;
        }
        String dayType = StringUtils.trimToEmpty(shareDO.getShareDayType()).toLowerCase();
        return "-1".equals(dayType) || dayType.contains("permanent") || dayType.contains("永久");
    }

    private void validateShareCodeIfNeeded(ShareDO shareDO, String shareCode) {
        if (!Objects.equals(shareDO.getShareType(), PRIVATE_SHARE_TYPE)) {
            return;
        }
        String normalizedCode = StringUtils.trimToEmpty(shareCode);
        if (StringUtils.isBlank(normalizedCode) || !StringUtils.equalsIgnoreCase(shareDO.getShareCode(), normalizedCode)) {
            throw new ShareException(ShareErrorCode.SHARE_CODE_ERROR);
        }
    }

    private ShareFileInfoVO toShareFileInfoVO(UserFileDO record) {
        ShareFileInfoVO vo = new ShareFileInfoVO();
        vo.setFileId(record.getId());
        vo.setFilename(record.getFilename());
        vo.setFolderFlag(record.getFolderFlag());
        vo.setFileType(record.getFileType());
        vo.setFileSizeDesc(record.getFileSizeDesc());
        vo.setUpdateTime(record.getGmtModified());
        return vo;
    }

    private void prepareDownloadResponse(HttpServletResponse response, String filename, String fileSize) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename=\"" + encodedFilename + "\";filename*=UTF-8''" + encodedFilename
        );
        if (StringUtils.isNumeric(fileSize)) {
            response.setContentLengthLong(Long.parseLong(fileSize));
        }
    }

    private String buildShareUrl(Long shareId) {
        String encryptedShareId = URLEncoder.encode(IdUtil.encrypt(shareId), StandardCharsets.UTF_8);
        if (shareAccessUrlPrefix.contains("{shareId}")) {
            return shareAccessUrlPrefix.replace("{shareId}", encryptedShareId);
        }
        if (shareAccessUrlPrefix.contains("?")) {
            String separator = shareAccessUrlPrefix.endsWith("?") || shareAccessUrlPrefix.endsWith("&") ? "" : "&";
            return shareAccessUrlPrefix + separator + "shareId=" + encryptedShareId;
        }
        return shareAccessUrlPrefix + "?shareId=" + encryptedShareId;
    }

    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    private void scheduleShareExpireDeleteJob(ShareDO shareDO) {
        if (shareDO == null || isPermanentShare(shareDO) || shareDO.getShareEndTime() == null) {
            return;
        }
        final long delayInMillis = Math.max(1000L, shareDO.getShareEndTime().getTime() - System.currentTimeMillis());
        DeleteShareInfoMessage message = new DeleteShareInfoMessage();
        message.setShareId(shareDO.getId());
        Runnable enqueue = () -> delayQueueHolder.addJob(
                message,
                delayInMillis,
                TimeUnit.MILLISECONDS,
                ShareConstant.DELETE_SHARE_INFO_DELAY_QUEUE_NAME
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enqueue.run();
                }
            });
            return;
        }
        enqueue.run();
    }

    private void removeShareExpireDeleteJob(Long shareId) {
        if (shareId == null) {
            return;
        }
        DeleteShareInfoMessage message = new DeleteShareInfoMessage();
        message.setShareId(shareId);
        try {
            delayQueueHolder.removeJob(message, ShareConstant.DELETE_SHARE_INFO_DELAY_QUEUE_NAME);
        } catch (Exception e) {
            log.warn("remove delayed share delete message failed, shareId={}", shareId, e);
        }
    }
}
