package com.disk.user;

import com.alicp.jetcache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NetworkdiskUserApplication.class})
public class UserBaseTest {

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private CacheManager cacheManager;

    @Test
    public void test(){

    }

}
