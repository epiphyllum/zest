/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.dao;

import io.renren.commons.mybatis.dao.BaseDao;
import io.renren.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUserEntity> {

    List<SysUserEntity> getList(Map<String, Object> params);

    SysUserEntity getById(Long id);

    SysUserEntity getByUsername(String username);

    int updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);

    /**
     * 根据部门ID，查询用户数
     */
    int getCountByDeptId(Long deptId);

    /**
     * 根据部门ID,查询用户ID列表
     */
    List<Long> getUserIdListByDeptId(List<Long> deptIdList);

    List<String> getRealNameList(List<Long> ids);

    List<Long> getUserIdListByRoleIdList(List<Long> ids);

    List<String> getRoleNameList(List<Long> ids);

    List<Long> getUserIdListByPostIdList(List<Long> ids);

    /**
     * 查询部门领导列表
     *
     * @param ids 部门列表
     */
    List<Long> getLeaderIdListByDeptIdList(List<Long> ids);

    /**
     * 获取用户部门领导ID
     *
     * @param userId 用户ID
     */
    Long getLeaderIdListByUserId(Long userId);

}