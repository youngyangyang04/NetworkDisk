package com.disk.user.domain.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.api.user.constant.UserOperateTypeEnum;
import com.disk.api.user.request.UserQueryRequest;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.response.UserOperatorResponse;
import com.disk.base.exception.BizException;
import com.disk.base.exception.RepoErrorCode;
import com.disk.user.domain.entity.UserDO;
import com.disk.user.infrastructure.exception.UserErrorCode;
import com.disk.user.infrastructure.exception.UserException;
import com.disk.user.infrastructure.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.disk.user.infrastructure.constant.UserConstant.DEFAULT_NICK_NAME_PREFIX;
import static com.disk.user.infrastructure.exception.UserErrorCode.DUPLICATE_TELEPHONE_NUMBER;

/**
 * 类描述: 用户服务
 *
 * @author weikunkun
 */
@Service
public class UserService extends ServiceImpl<UserMapper, UserDO> implements InitializingBean {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserMapper userMapper;

    private RBloomFilter<String> nickNameBloomFilter;

    private RBloomFilter<String> inviteCodeBloomFilter;

    private RScoredSortedSet<String> inviteRank;


    /**
     * TODO 注册、认证、激活、冻结、解禁
     * @throws Exception
     */

    /**
     * 根据id查询用户信息
     *
     * @param telephone
     * @return
     */
    @Cached(name = "user:cached:telephone:", expire = 3000, cacheType = CacheType.BOTH, key = "#telephone", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.HOURS)
    public UserDO findByTelephone(String telephone) {
       return userMapper.findByTelephone(telephone);
    }


    /**
     * 根据id查询用户信息
     *
     * @param userId
     * @return
     */
    @Cached(name = "user:cached:id:", expire = 3000, cacheType = CacheType.BOTH, key = "#userId", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.HOURS)
    public UserDO findById(Long userId) {
        UserDO userDO = userMapper.findById(userId);
        return userDO;
    }

    /**
     * 通过手机号和密码查询用户信息
     *
     * @param telephone
     * @param password
     * @return
     */
    public UserDO findByTelephoneAndPass(String telephone, String password) {
        return userMapper.findByTelephoneAndPass(telephone, DigestUtil.md5Hex(password));
    }

    /**
     * 用户注册
     * @param telephone
     * @param inviteCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public UserOperatorResponse register(String telephone, String inviteCode) {
        String defaultNickName;
        String randomString;
        do {
            randomString = RandomUtil.randomString(6).toUpperCase();
            //前缀 + 6位随机数 + 手机号后四位
            defaultNickName = DEFAULT_NICK_NAME_PREFIX + randomString + telephone.substring(7, 11);
        } while (nickNameExist(defaultNickName) || inviteCodeExist(randomString));

        String inviterId = null;
        if (StringUtils.isNotBlank(inviteCode)) {
            UserDO inviter = userMapper.findByInviteCode(inviteCode);
            if (inviter != null) {
                inviterId = inviter.getId().toString();
            }
        }

        UserDO user = doRegister(telephone, defaultNickName, telephone, randomString, inviterId);
        Assert.notNull(user, UserErrorCode.USER_OPERATE_FAILED.getCode());

        addNickName(defaultNickName);
        addInviteCode(randomString);
        updateInviteRank(inviterId);
//        updateUserCache(user.getId().toString(), user);

        //TODO 加入流水

        UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
        userOperatorResponse.setSuccess(true);

        return userOperatorResponse;
    }


    public boolean nickNameExist(String nickName) {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return userMapper.findByNickname(nickName) != null;
        }

        return false;
    }

    public boolean inviteCodeExist(String inviteCode) {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (this.inviteCodeBloomFilter != null && this.inviteCodeBloomFilter.contains(inviteCode)) {
            return userMapper.findByInviteCode(inviteCode) != null;
        }

        return false;
    }

    /**
     * 注册
     *
     * @param telephone
     * @param nickName
     * @param password
     * @return
     */
    private UserDO doRegister(String telephone, String nickName, String password, String inviteCode, String inviterId) {
        if (userMapper.findByTelephone(telephone) != null) {
            throw new UserException(DUPLICATE_TELEPHONE_NUMBER);
        }

        UserDO user = new UserDO();
        user.register(telephone, nickName, password, inviteCode, inviterId);
//        user.setRealName("abc");
//        user.setDeleted(0);
//        user.setGmtCreate(new Date());
//        user.setGmtModified(new Date());
//        user.setLockVersion(0);
        return save(user) ? user : null;
    }

    private boolean addNickName(String nickName) {
        return this.nickNameBloomFilter != null && this.nickNameBloomFilter.add(nickName);
    }

    private boolean addInviteCode(String inviteCode) {
        return this.inviteCodeBloomFilter != null && this.inviteCodeBloomFilter.add(inviteCode);
    }

    private void updateInviteRank(String inviterId) {
        if (inviterId == null) {
            return;
        }
        RLock rLock = redissonClient.getLock(inviterId);
        rLock.lock();
        try {
            Double score = inviteRank.getScore(inviterId);
            if (score == null) {
                score = 0.0;
            }
            inviteRank.add(score + 100.0, inviterId);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.nickNameBloomFilter = redissonClient.getBloomFilter("nickName");
        if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists()) {
            this.nickNameBloomFilter.tryInit(100000L, 0.01);
        }

        this.inviteCodeBloomFilter = redissonClient.getBloomFilter("inviteCode");
        if (inviteCodeBloomFilter != null && !inviteCodeBloomFilter.isExists()) {
            this.inviteCodeBloomFilter.tryInit(100000L, 0.01);
        }

        this.inviteRank = redissonClient.getScoredSortedSet("inviteRank");
    }
}
