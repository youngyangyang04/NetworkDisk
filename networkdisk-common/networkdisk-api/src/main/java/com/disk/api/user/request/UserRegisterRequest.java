package com.disk.api.user.request;

import com.disk.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weikunkun
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseRequest {

    private String email;

    private String password;

    private String nickname;

}
