package com.disk.share.domain.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.share.domain.entity.ShareDO;
import com.disk.share.domain.entity.ShareFileDO;
import com.disk.share.domain.service.ShareFileService;
import com.disk.share.domain.service.ShareService;
import com.disk.share.infrastructure.mapper.ShareFileMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
 * 类描述: 分享和文件映射服务
 *
 * @author weikunkun
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFileDO> implements ShareFileService {
}
