package com.disk.api.notice.service;

import com.disk.api.notice.response.NoticeResponse;

/**
 * 类描述: 消息
 *
 * @author weikunkun
 */
public interface NoticeFacadeService {

    NoticeResponse generateAndSendSmsCaptcha(String telephone);
}
