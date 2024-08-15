/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service;

import io.renren.commons.mybatis.service.BaseService;
import io.renren.commons.tools.page.PageData;
import io.renren.dto.SysRoleDTO;
import io.renren.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public interface SysRoleService extends BaseService<SysRoleEntity> {

    PageData<SysRoleDTO> page(Map<String, Object> params);

    List<SysRoleDTO> list(Map<String, Object> params);

    SysRoleDTO get(Long id);

    void save(SysRoleDTO dto);

    void update(SysRoleDTO dto);

    void delete(Long[] ids);

}