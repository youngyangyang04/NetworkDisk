package com.disk.user.domain.response;

import com.disk.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
public class UserInfoVO {

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    private String nickname;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;

    private String rootFilename;

    private String profilePhotoUrl;
}
