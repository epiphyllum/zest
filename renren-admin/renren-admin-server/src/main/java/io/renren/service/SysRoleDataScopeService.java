/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service;

import io.renren.commons.mybatis.service.BaseService;
import io.renren.entity.SysRoleDataScopeEntity;

import java.util.List;

/**
 * 角色数据权限
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public interface SysRoleDataScopeService extends BaseService<SysRoleDataScopeEntity> {

    /**
     * 根据角色ID，获取部门ID列表
     */
    List<Long> getDeptIdList(Long roleId);

    /**
     * 获取用户对应的部门数据权限
     * @param userId  用户ID
     * @return        返回部门ID列表
     */
    List<Long> getDataScopeList(Long userId);

    /**
     * 保存或修改
     * @param roleId      角色ID
     * @param deptIdList  部门ID列表
     */
    void saveOrUpdate(Long roleId, List<Long> deptIdList);

    /**
     * 根据角色id，删除角色数据权限关系
     * @param roleId 角色id
     */
    void deleteByRoleId(Long roleId);
}