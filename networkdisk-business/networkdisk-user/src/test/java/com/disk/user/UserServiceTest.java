package com.disk.user;

import com.disk.user.domain.entity.UserDO;
import com.disk.user.domain.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class UserServiceTest extends UserBaseTest{

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        UserDO user = new UserDO();
        userService.save(user);
    }
}
