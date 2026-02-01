package com.disk.user.facade;

import com.disk.api.user.request.UserActiveRequest;
import com.disk.api.user.request.UserAuthRequest;
import com.disk.api.user.request.UserModifyRequest;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.request.condition.UserIdQueryCondition;
import com.disk.api.user.request.condition.UserEmailQueryCondition;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.api.user.response.UserQueryResponse;
import com.disk.api.user.response.data.UserInfo;
import com.disk.api.user.service.UserFacadeService;
import com.disk.rpc.facade.Facade;
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

    @Facade
    @Override
    public UserQueryResponse<UserInfo> query(UserQueryRequest request) {
        UserDO userDO = switch (request.getUserQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getUserId());
            case UserEmailQueryCondition userEmailQueryCondition:
                yield userService.findByEmail(userEmailQueryCondition.getEmail());
            default:
                throw new UnsupportedOperationException(request.getUserQueryCondition() + "'' is not supported");
        };

        UserQueryResponse<UserInfo> response = new UserQueryResponse();
        UserInfo userInfoVO = UserConvertor.INSTANCE.mapToVo(userDO);
        response.setSuccess(true);
        response.setData(userInfoVO);
        return response;
    }

    @Facade
    @Override
    public UserOperatorResponse<UserInfo> register(UserRegisterRequest request) {
        UserDO register = userService.register(request);
        UserOperatorResponse<UserInfo> response = new UserOperatorResponse<>();
        UserInfo userInfoVO = UserConvertor.INSTANCE.mapToVo(register);
        response.setSuccess(true);
        response.setData(userInfoVO);
        return response;
    }

    @Facade
    @Override
    public UserOperatorResponse modify(UserModifyRequest request) {
        return null;
    }

    @Facade
    @Override
    public UserOperatorResponse auth(UserAuthRequest request) {
        return null;
    }

    @Facade
    @Override
    public UserOperatorResponse active(UserActiveRequest request) {
        return null;
    }
}
