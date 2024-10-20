package com.disk.user.facade;

import com.disk.api.user.request.UserActiveRequest;
import com.disk.api.user.request.UserAuthRequest;
import com.disk.api.user.request.UserModifyRequest;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.request.condition.UserIdQueryCondition;
import com.disk.api.user.request.condition.UserPhoneAndPasswordQueryCondition;
import com.disk.api.user.request.condition.UserPhoneQueryCondition;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.api.user.response.UserQueryResponse;
import com.disk.api.user.response.data.UserInfo;
import com.disk.api.user.service.UserFacadeService;
import com.disk.user.domain.entity.UserDO;
import com.disk.user.domain.entity.convertor.UserConvertor;
import com.disk.user.domain.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@DubboService(version = "1.0.0")
public class UserFacadeServiceImpl implements UserFacadeService {

    @Autowired
    private UserService userService;

    @Override
    public UserQueryResponse<UserInfo> query(UserQueryRequest request) {
        UserDO userDO = switch (request.getUserQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getUserId());
            case UserPhoneQueryCondition userPhoneQueryCondition:
                yield userService.findByTelephone(userPhoneQueryCondition.getTelephone());
            case UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition:
                yield userService.findByTelephoneAndPass(userPhoneAndPasswordQueryCondition.getTelephone(), userPhoneAndPasswordQueryCondition.getPassword());
            default:
                throw new UnsupportedOperationException(request.getUserQueryCondition() + "'' is not supported");
        };

        UserQueryResponse<UserInfo> response = new UserQueryResponse();
        response.setSuccess(true);
        UserInfo userInfoVO = UserConvertor.INSTANCE.mapToVo(userDO);
        response.setData(userInfoVO);
        return response;
    }

    @Override
    public UserOperatorResponse register(UserRegisterRequest request) {
        return null;
    }

    @Override
    public UserOperatorResponse modify(UserModifyRequest request) {
        return null;
    }

    @Override
    public UserOperatorResponse auth(UserAuthRequest request) {
        return null;
    }

    @Override
    public UserOperatorResponse active(UserActiveRequest request) {
        return null;
    }
}
