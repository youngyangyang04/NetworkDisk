package com.disk.share.job.delay;

import com.disk.base.utils.EmptyUtil;
import com.disk.delayqueue.DelayQueueHolder;
import com.disk.delayqueue.executor.DelayQueueExecutor;
import com.disk.share.domain.context.DeleteShareContext;
import com.disk.share.domain.service.ShareService;
import com.disk.share.infrastructure.constant.ShareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类描述: 分享文件删除执行器
 *
 * @author weikunkun
 */
@Slf4j
@Component
public class DeleteShareInfoDelayQueueExecutor implements DelayQueueExecutor<DeleteShareInfoMessage> {

    @Autowired
    private DelayQueueHolder delayQueueHolder;

    @Autowired
    private ShareService shareService;

    @Override
    public String queueName() {
        return ShareConstant.DELETE_SHARE_INFO_DELAY_QUEUE_NAME;
    }

    @Override
    public void execute(DeleteShareInfoMessage deleteShareInfoMessage) {
        Long shareId = deleteShareInfoMessage.getShareId();
        log.info("start delete share info, shareId: [{}]", shareId);
        if (EmptyUtil.isEmpty(shareId)) {
            return;
        }
        try {
            DeleteShareContext context = new DeleteShareContext();
            context.setShareId(shareId);
            shareService.deleteUserShare(context);
        } catch (Exception e) {
            log.error("delete share info error", e);
        }

    }

    @Override
    public DeleteShareInfoMessage take() throws InterruptedException {
        return null;
    }
}
