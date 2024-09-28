package io.renren.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.commons.security.cache.TokenStoreCache;
import io.renren.commons.security.properties.SecurityProperties;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.security.utils.TokenUtils;
import io.renren.commons.tools.exception.ErrorCode;
import io.renren.commons.tools.exception.RenException;
import io.renren.dao.SysDeptDao;
import io.renren.dao.SysUserTokenDao;
import io.renren.dto.UserTokenDTO;
import io.renren.entity.SysDeptEntity;
import io.renren.entity.SysUserTokenEntity;
import io.renren.service.SysUserDetailService;
import io.renren.service.SysUserTokenService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
@AllArgsConstructor
public class SysUserTokenServiceImpl extends BaseServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
    private final TokenStoreCache tokenStoreCache;
    private final SecurityProperties securityProperties;
    private final SysUserDetailService sysUserDetailService;

    private final SysDeptDao sysDeptDao;

    @Override
    public UserTokenDTO createToken(Long userId) {
        // 生成token
        String accessToken = TokenUtils.generator();
        String refreshToken = TokenUtils.generator();

        SysUserTokenEntity entity = new SysUserTokenEntity();
        entity.setUserId(userId);
        entity.setAccessToken(accessToken);
        entity.setRefreshToken(refreshToken);

        // 过期时间
        Date now = new Date();
        entity.setAccessTokenExpire(DateUtil.offsetSecond(now, securityProperties.getAccessTokenExpire()));
        entity.setRefreshTokenExpire(DateUtil.offsetSecond(now, securityProperties.getRefreshTokenExpire()));

        // 是否存在Token
        SysUserTokenEntity tokenEntity = baseDao.selectOne(new LambdaQueryWrapper<SysUserTokenEntity>().eq(SysUserTokenEntity::getUserId, userId));
        if (tokenEntity == null) {
            baseDao.insert(entity);
        } else {
            entity.setId(tokenEntity.getId());
            baseDao.updateById(entity);
        }

        return Convert.convert(UserTokenDTO.class, entity);
    }

    @Override
    public UserTokenDTO refreshToken(String refreshToken) {
        LambdaQueryWrapper<SysUserTokenEntity> query = Wrappers.lambdaQuery();
        query.eq(SysUserTokenEntity::getRefreshToken, refreshToken);
        query.ge(SysUserTokenEntity::getRefreshTokenExpire, new Date());

        // 不存在，则表示refreshToken错误，或者已过期
        SysUserTokenEntity entity = baseDao.selectOne(query);
        if (entity == null) {
            throw new RenException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 删除缓存信息
        tokenStoreCache.deleteUser(entity.getAccessToken());

        // 生成新 accessToken
        String accessToken = TokenUtils.generator();
        entity.setAccessToken(accessToken);
        entity.setAccessTokenExpire(DateUtil.offsetSecond(new Date(), securityProperties.getAccessTokenExpire()));

        // 更新
        baseDao.updateById(entity);

        // 用户权限
        UserDetail userDetail = sysUserDetailService.getUserDetailById(entity.getUserId());

        // 依据用户部门pids确定用户的类型
        setUserType(userDetail);

        // 保存用户信息到缓存
        tokenStoreCache.saveUser(accessToken, userDetail);

        return Convert.convert(UserTokenDTO.class, entity);
    }

    @Override
    public void expireToken(Long userId) {
        SysUserTokenEntity entity = new SysUserTokenEntity();
        entity.setAccessTokenExpire(new Date());
        entity.setRefreshTokenExpire(new Date());

        baseDao.update(entity, new LambdaQueryWrapper<SysUserTokenEntity>().eq(SysUserTokenEntity::getUserId, userId));
    }

    @Async
    @Override
    public void updateCacheAuthByRoleId(Long roleId) {
        // 根据角色ID，查询用户 access_token 列表
        List<String> accessTokenList = baseDao.getOnlineAccessTokenListByRoleId(roleId, new Date());

        accessTokenList.forEach(this::updateCacheAuth);
    }

    @Async
    @Override
    public void updateCacheAuthByUserId(Long userId) {
        // 根据用户ID，查询用户 access_token 列表
        List<String> accessTokenList = baseDao.getOnlineAccessTokenListByUserId(userId, new Date());

        accessTokenList.forEach(this::updateCacheAuth);
    }

    /**
     * 根据accessToken，更新Cache里面的用户权限
     *
     * @param accessToken access_token
     */
    private void updateCacheAuth(String accessToken) {
        UserDetail user = tokenStoreCache.getUser(accessToken);
        // 用户不存在
        if (user == null) {
            return;
        }

        // 查询过期时间
        Long expire = tokenStoreCache.getExpire(accessToken);
        if (expire == null) {
            return;
        }

        // 用户权限
        user = sysUserDetailService.getUserDetailById(user.getId());
        setUserType(user);

        // 更新缓存
        tokenStoreCache.saveUser(accessToken, user, expire);
    }

    private void setUserType(UserDetail userDetail) {
        SysDeptEntity deptEntity = sysDeptDao.selectById(userDetail.getDeptId());
        String[] split = deptEntity.getPids().split(",");
        if (split.length == 2) {
            userDetail.setUserType("merchant");
        } else if (split.length == 3) {
            userDetail.setUserType("sub");
        } else if (split.length == 1) {
            if (split[0].equals("0")) {
                userDetail.setUserType("operation");
            } else {
                userDetail.setUserType("agent");
            }
        } else {
            throw new RenException("internal error");
        }
    }

}