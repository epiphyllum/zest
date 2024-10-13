package io.renren.service.impl;

import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.dao.SysUserDao;
import io.renren.entity.SysDeptEntity;
import io.renren.entity.SysUserEntity;
import io.renren.redis.SysMenuRedis;
import io.renren.service.SysMenuService;
import io.renren.service.SysRoleDataScopeService;
import io.renren.service.SysUserDetailService;
import io.renren.zcommon.ZestConstant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * UserDetail Service
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysUserDetailServiceImpl implements SysUserDetailService {
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysMenuRedis sysMenuRedis;
    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysRoleDataScopeService sysRoleDataScopeService;

    @Override
    public UserDetail getUserDetailById(Long id) {
        SysUserEntity user = sysUserDao.getById(id);

        UserDetail userDetail = ConvertUtils.sourceToTarget(user, UserDetail.class);
        initUserData(userDetail);

        return userDetail;
    }

    @Override
    public UserDetail getUserDetailByUsername(String username) {
        SysUserEntity user = sysUserDao.getByUsername(username);

        UserDetail userDetail = ConvertUtils.sourceToTarget(user, UserDetail.class);


        initUserData(userDetail);

        return userDetail;
    }

    /**
     * 初始化用户数据
     */
    private void initUserData(UserDetail userDetail) {
        if (userDetail == null) {
            return;
        }

        //清空当前用户，菜单导航、权限标识
        sysMenuRedis.delete(userDetail.getId());

        //用户部门数据权限
        List<Long> deptIdList = sysRoleDataScopeService.getDataScopeList(userDetail.getId());
        userDetail.setDeptIdList(deptIdList);

        //获取用户权限标识
        Set<String> authorities = sysMenuService.getUserPermissions(userDetail);
        userDetail.setAuthoritySet(authorities);

        if(userDetail.getDeptId() != null) {
            SysDeptEntity deptEntity = sysDeptDao.selectById(userDetail.getDeptId());
            userDetail.setDeptName(deptEntity.getName());
        }

        // 依据用户部门pids确定用户的类型
        setUserType(userDetail);
    }

    private void setUserType(UserDetail userDetail) {
        if (userDetail.getSuperAdmin().equals(1)) {
            return;
        }

        SysDeptEntity deptEntity = sysDeptDao.selectById(userDetail.getDeptId());
        if (deptEntity == null) {
            return;
        }

        String[] split = deptEntity.getPids().split(",");
        if (split.length == 2) {
            userDetail.setUserType(ZestConstant.USER_TYPE_MERCHANT);
        } else if (split.length == 3) {
            userDetail.setUserType(ZestConstant.USER_TYPE_SUB);
        } else if (split.length == 1) {
            if (split[0].equals("0")) {
                userDetail.setUserType(ZestConstant.USER_TYPE_OPERATION);
            } else {
                userDetail.setUserType(ZestConstant.USER_TYPE_AGENT);
            }
        } else {
            throw new RenException("internal error");
        }
    }
}
