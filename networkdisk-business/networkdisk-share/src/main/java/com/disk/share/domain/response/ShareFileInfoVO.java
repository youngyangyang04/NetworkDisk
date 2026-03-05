package com.disk.share.domain.response;

import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Shared file info shown on public share page.
 */
@Getter
@Setter
public class ShareFileInfoVO {

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;

    private String filename;

    private Integer folderFlag;

    private Integer fileType;

    private String fileSizeDesc;

    private Date updateTime;
}
