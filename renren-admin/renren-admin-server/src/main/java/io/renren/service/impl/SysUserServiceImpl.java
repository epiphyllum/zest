/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.enums.SuperAdminEnum;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.dao.SysUserDao;
import io.renren.dto.SysUserDTO;
import io.renren.entity.SysDeptEntity;
import io.renren.entity.SysUserEntity;
import io.renren.service.*;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    @Resource
    private SysRoleUserService sysRoleUserService;
    @Resource
    private SysDeptService sysDeptService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SysUserPostService sysUserPostService;
    @Resource
    private SysUserTokenService sysUserTokenService;
    @Resource
    private SysDeptDao sysDeptDao;

    @Override
    public PageData<SysUserDTO> page(Map<String, Object> params) {
        //转换成like
        paramsToLike(params, "username");

        //分页
        IPage<SysUserEntity> page = getPage(params, "t1.create_date", false);

        //普通管理员，只能查询所属部门及子部门的数据
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            params.put("deptIdList", sysDeptService.getSubDeptIdList(user.getDeptId()));
        }

        //查询
        List<SysUserEntity> list = baseDao.getList(params);

        return getPageData(list, page.getTotal(), SysUserDTO.class);
    }

    @Override
    public List<SysUserDTO> list(Map<String, Object> params) {
        //普通管理员，只能查询所属部门及子部门的数据
        UserDetail user = SecurityUser.getUser();
        if (user.getSuperAdmin() == SuperAdminEnum.NO.value()) {
            params.put("deptIdList", sysDeptService.getSubDeptIdList(user.getDeptId()));
        }

        List<SysUserEntity> entityList = baseDao.getList(params);

        return ConvertUtils.sourceToTarget(entityList, SysUserDTO.class);
    }

    @Override
    public SysUserDTO get(Long id) {
        SysUserEntity entity = baseDao.getById(id);

        SysUserDTO sysUserDTO = ConvertUtils.sourceToTarget(entity, SysUserDTO.class);

        // 超级管理员
        if (sysUserDTO.getSuperAdmin().equals(1)) {
            return sysUserDTO;
        }

        // 确定用户类型
        SysDeptEntity deptEntity = sysDeptDao.selectById(entity.getDeptId());
        if (deptEntity == null) {
            return sysUserDTO;
        }

        String[] split = deptEntity.getPids().split(",");
        if (split.length == 3) {
            sysUserDTO.setUserType(ZestConstant.USER_TYPE_SUB);
        } else if (split.length == 2) {
            sysUserDTO.setUserType(ZestConstant.USER_TYPE_MERCHANT);
        } else if (split.length == 1) {
            if (split[0].equals("0")) {
                sysUserDTO.setUserType(ZestConstant.USER_TYPE_OPERATION);
            } else {
                sysUserDTO.setUserType(ZestConstant.USER_TYPE_AGENT);
            }
        } else {
            throw new RenException("internal error");
        }

        // 返回
        return sysUserDTO;
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = baseDao.getByUsername(username);
        return ConvertUtils.sourceToTarget(entity, SysUserDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUserDTO dto) {
        SysUserEntity entity = ConvertUtils.sourceToTarget(dto, SysUserEntity.class);

        //密码加密
        String password = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(password);

        //保存用户
        entity.setSuperAdmin(SuperAdminEnum.NO.value());
        insert(entity);

        //保存角色用户关系
        sysRoleUserService.saveOrUpdate(entity.getId(), dto.getRoleIdList());

        //保存用户岗位关系
        sysUserPostService.saveOrUpdate(entity.getId(), dto.getPostIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserDTO dto) {
        SysUserEntity entity = ConvertUtils.sourceToTarget(dto, SysUserEntity.class);

        //密码加密
        if (StringUtils.isBlank(dto.getPassword())) {
            entity.setPassword(null);
        } else {
            String password = passwordEncoder.encode(entity.getPassword());
            entity.setPassword(password);
        }

        //更新用户
        updateById(entity);

        //更新角色用户关系
        sysRoleUserService.saveOrUpdate(entity.getId(), dto.getRoleIdList());

        //保存用户岗位关系
        sysUserPostService.saveOrUpdate(entity.getId(), dto.getPostIdList());

        //更新用户缓存权限
        sysUserTokenService.updateCacheAuthByUserId(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(SysUserDTO dto) {
        SysUserEntity entity = selectById(dto.getId());
        entity.setHeadUrl(dto.getHeadUrl());
        entity.setRealName(dto.getRealName());
        entity.setGender(dto.getGender());
        entity.setMobile(dto.getMobile());
        entity.setEmail(dto.getEmail());

        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //逻辑删除
        logicDelete(ids, SysUserEntity.class);

        //角色用户关系，岗位关系需要保留，不然逻辑删除就变成物理删除了
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, String newPassword) {
        newPassword = passwordEncoder.encode(newPassword);

        baseDao.updatePassword(id, newPassword);
    }

    @Override
    public int getCountByDeptId(Long deptId) {
        return baseDao.getCountByDeptId(deptId);
    }

    @Override
    public List<Long> getUserIdListByDeptId(List<Long> deptIdList) {
        return baseDao.getUserIdListByDeptId(deptIdList);
    }

    @Override
    public List<String> getRealNameList(List<Long> ids) {
        return baseDao.getRealNameList(ids);
    }

    @Override
    public List<Long> getUserIdListByRoleIdList(List<Long> ids) {
        return baseDao.getUserIdListByRoleIdList(ids);
    }

    @Override
    public List<String> getRoleNameList(List<Long> ids) {
        return baseDao.getRoleNameList(ids);
    }

    @Override
    public List<Long> getUserIdListByPostIdList(List<Long> ids) {
        return baseDao.getUserIdListByPostIdList(ids);
    }

    @Override
    public List<Long> getLeaderIdListByDeptIdList(List<Long> ids) {
        return baseDao.getLeaderIdListByDeptIdList(ids);
    }

    @Override
    public Long getLeaderIdListByUserId(Long userId) {
        return baseDao.getLeaderIdListByUserId(userId);
    }

}