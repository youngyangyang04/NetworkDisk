package com.disk.user;

import com.disk.user.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
public class UserDaoTest extends UserBaseTest{

    @Autowired
    private UserMapper userMapper;


    @Test
    public void testAddUser() {
    }
}
