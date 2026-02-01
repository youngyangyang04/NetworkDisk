package com.disk.mq.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Data
@Accessors(chain = true)
public class MessageBody {

    /**
     * 幂等号
     */
    private String identifier;
    /**
     * 消息体
     */
    private String body;
}
