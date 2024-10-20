package com.disk.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.response.data.UserInfo;
import com.disk.user.domain.entity.UserDO;
import com.disk.user.domain.entity.convertor.UserConvertor;
import com.disk.user.domain.service.UserService;
import com.disk.user.infrastructure.exception.UserException;
import com.disk.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.disk.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;

/**
 * 类描述: 用户信息
 *
 * @author weikunkun
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/get-user-info")
    public Result<UserInfo>  getUserInfo() {
        String userId = (String) StpUtil.getLoginId();
        UserQueryRequest userQueryRequest = new UserQueryRequest();
//        userQueryRequest.setUserId(Long.valueOf(userId));
        UserDO user = userService.findById(Long.valueOf(userId));

        if (null == user) {
            throw new UserException(USER_NOT_EXIST);
        }
        return Result.success(UserConvertor.INSTANCE.mapToVo(user));

    }

    @GetMapping("/test/{id}")
    public Result<UserDO>  test(@PathVariable Long id) {
        UserDO userDO = userService.findById(Long.valueOf(id));
        return Result.success(userDO);
    }

}
