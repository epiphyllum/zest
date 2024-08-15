/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.service;

import io.renren.commons.security.enums.UserStatusEnum;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.ErrorCode;
import io.renren.commons.tools.exception.RenException;
import io.renren.service.SysUserDetailService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class RenUserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private SysUserDetailService sysUserDetailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetail userDetail = sysUserDetailService.getUserDetailByUsername(username);
        if (userDetail == null) {
            throw new RenException(ErrorCode.ACCOUNT_NOT_EXIST);
        }

        // 账号不可用
        if (userDetail.getStatus() == UserStatusEnum.DISABLE.value()) {
            userDetail.setEnabled(false);
        }

        return userDetail;
    }
}
