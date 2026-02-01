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
public class Message {

    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息体
     */
    private String body;
}
