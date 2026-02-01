package com.disk.share.job.delay;

import com.disk.delayqueue.DelayMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
public class DeleteShareInfoMessage extends DelayMessage {

    private Long shareId;

}
