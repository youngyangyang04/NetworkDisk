package com.disk.user.domain.service;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.disk.api.files.request.UserFileQueryRequest;
import com.disk.api.files.response.UserFileQueryResponse;
import com.disk.api.files.response.data.UserFileData;
import com.disk.api.files.service.UserFileFacadeService;
import com.disk.api.user.request.UserRegisterRequest;
import com.disk.api.user.service.UserFacadeService;
import com.disk.base.enums.DeleteEnum;
import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.IdUtil;
import com.disk.base.utils.PasswordUtil;
import com.disk.user.controller.UserController;
import com.disk.user.domain.entity.UserDO;
import com.disk.user.domain.entity.convertor.UserConvertor;
import com.disk.user.domain.response.UserInfoVO;
import com.disk.user.infrastructure.exception.UserException;
import com.disk.user.infrastructure.mapper.UserMapper;
import com.disk.user.infrastructure.util.UserConstants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.disk.user.infrastructure.exception.UserErrorCode.DUPLICATE_TELEPHONE_NUMBER;
import static com.disk.user.infrastructure.exception.UserErrorCode.USER_INFO_FAIL;
import static com.disk.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;

/**
 * 类描述: 用户服务
 *
 * @author weikunkun
 */
@Service
public class UserService extends ServiceImpl<UserMapper, UserDO> implements InitializingBean {

    @DubboReference(version = "1.0.0")
    private UserFileFacadeService userFileFacadeService;


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
     * @param email
     * @return
     */
    @Cached(name = "user:cached:telephone:", expire = 3000, cacheType = CacheType.BOTH, key = "#telephone", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.HOURS)
    public UserDO findByEmail(String email) {
       return userMapper.findByEmail(email);
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


    public boolean nickNameExist(String nickName) {
        //如果布隆过滤器中存在，再进行数据库二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return userMapper.findByNickname(nickName) != null;
        }

        return false;
    }

    public UserDO register(UserRegisterRequest request) {
        UserDO insertDO = new UserDO();
        insertDO.setEmail(request.getEmail());
        insertDO.setNickName(request.getNickname());
        insertDO.setId(IdUtil.get());
        insertDO.setPasswordHash(PasswordUtil.encryptPassword(request.getPassword()));
        insertDO.setUseSpace(UserConstants.USER_INIT_SPACE);
        insertDO.setDeleted(DeleteEnum.NO.getCode());
        insertDO.setLastLoginTime(new Date());
        insertDO.setProfilePhotoUrl("https://avatars.githubusercontent.com/u/25891014?v=4");
        userMapper.insert(insertDO);
        return insertDO;
    }

    /**
     * 注册
     *
     * @param email
     * @param nickName
     * @param password
     * @return
     */
    private UserDO doRegister(String email, String nickName, String password) {
        if (userMapper.findByEmail(email) != null) {
            throw new UserException(DUPLICATE_TELEPHONE_NUMBER);
        }

        UserDO user = new UserDO();
        user.register(email, nickName, password);
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

    public UserInfoVO getUserInfo(Long userId) {
        UserDO userDO = this.findById(userId);
        if (EmptyUtil.isEmpty(userDO)) {
            throw new UserException(USER_NOT_EXIST);
        }
        UserFileQueryRequest userFileQueryRequest = new UserFileQueryRequest(userId);
        UserFileQueryResponse<UserFileData> userFileInfo = userFileFacadeService.getUserFileInfo(userFileQueryRequest);
        UserFileData data = userFileInfo.getData();
        if (EmptyUtil.isEmpty(data)) {
            throw new UserException(USER_INFO_FAIL);
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setNickname(userDO.getNickName());
        userInfoVO.setRootFileId(data.getId());
        userInfoVO.setRootFilename(data.getFilename());
        userInfoVO.setUserId(data.getUserId());
        userInfoVO.setProfilePhotoUrl(userDO.getProfilePhotoUrl());
        return userInfoVO;
    }
}
